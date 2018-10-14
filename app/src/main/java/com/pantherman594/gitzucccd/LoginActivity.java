package com.pantherman594.gitzucccd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.graphics.BitmapFactory.decodeFile;

public class LoginActivity extends AppCompatActivity {

    // Login to facebook via WebView
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView rotateImage = (ImageView) findViewById(R.id.rotate_image);
        Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);
        rotateImage.startAnimation(startRotateAnimation);

        WebView browser = findViewById(R.id.login_webview);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(this, "HTMLOUT");

        // Send to the search activity once logged in
        WebViewClient logInClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("home.php")) sendToSearch();
            }
        };

        browser.setWebViewClient(logInClient);
        browser.loadUrl("https://m.facebook.com/login.php");
    }

    private void sendToSearch() {

        // Attempt to load saved data from the cache
        boolean found = false;
        getFilesDir().mkdir();

        for (File file : getFilesDir().listFiles()) {
            if (file.getName().endsWith(".dat")) {
                try {
                    InputStream inputStream = this.openFileInput(file.getName());

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString;
                        StringBuilder dataBuilder = new StringBuilder();

                        while ((receiveString = bufferedReader.readLine()) != null) {
                            dataBuilder.append(receiveString);
                        }

                        inputStream.close();

                        String[] data = dataBuilder.toString().split(";;;");
                        // Delete the data file if it's incomplete
                        if (data.length != 3) {
                            file.delete();
                            continue;
                        }
                        String name = data[0];
                        String username = data[1];
                        boolean isId = Boolean.valueOf(data[2]);

                        Bitmap profImg = decodeFile(new File(getFilesDir(), username + ".png").toString());

                        if (profImg == null) {
                            file.delete();
                            continue;
                        }

                        Friend friend = new Friend(name, username, isId, profImg);
                        Friend.addFriend(friend);

                        if (!found) {
                            found = true;
                            findViewById(R.id.login_webview).setVisibility(View.INVISIBLE);
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }
            }
        }

        if (found) {
            // If there is a cache, send the user directly to the camera.
            Intent sendToCamera = new Intent(LoginActivity.this, CameraActivity.class);
            startActivity(sendToCamera);
        } else {
            // If there is no cache, first load SearchActivity to download friends
            Intent sendToSearch = new Intent(LoginActivity.this, SearchActivity.class);
            startActivity(sendToSearch);
        }
    }
}
