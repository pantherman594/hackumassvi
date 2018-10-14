package com.pantherman594.gitzucccd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import static android.graphics.BitmapFactory.decodeFile;

public class LoginActivity extends AppCompatActivity {

    private static final List<String> PERMISSIONS = Arrays.asList("public_profile", "user_friends");
    private CallbackManager callbackManager;

    // Login to facebook api
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(PERMISSIONS);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        sendToSearch();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Login Cancelled!",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            Log.d("BBBBBBBBBB", "already logged in");
            sendToSearch();
        } else {
            // Start login
            LoginManager.getInstance().logInWithReadPermissions(this, PERMISSIONS);
        }*/

        // Get the webview from the application
        WebView browser = findViewById(R.id.login_webview);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(this, "HTMLOUT");

        final WebViewClient logInClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            if (url.contains("home.php")) sendToSearch();
            }
        };

        browser.setWebViewClient(logInClient);
        browser.loadUrl("https://m.facebook.com/login.php");
        Log.d("BBBBBBBBBBBLLLLL", "Load");
    }

    private void sendToSearch() {
        boolean found = false;
        getFilesDir().mkdir();
        for (File file : getFilesDir().listFiles()) {
            if (file.getName().endsWith(".dat")) {
                try {
                    InputStream inputStream = this.openFileInput(file.getName());

                    if ( inputStream != null ) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();

                        Log.d("AAAAAAAAAAAA", stringBuilder.toString());
                        String[] data = stringBuilder.toString().split(";;;");
                        if (data.length != 3) {
                            file.delete();
                            continue;
                        }
                        String name = data[0];
                        String username = data[1];
                        boolean isId = Boolean.valueOf(data[2]);

                        Bitmap profImg = decodeFile(username + ".png");

                        if (profImg == null) {
                            file.delete();
                            continue;
                        }

                        Friend friend = new Friend(name, username, isId, profImg);
                        Friend.addFriend(friend);

                        found = true;
                    }
                }
                catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }
            }
        }

        Log.d("FFFFF", "f" + found);
        if (found) {
            Intent sendToCamera = new Intent(LoginActivity.this, CameraActivity.class);
            startActivity(sendToCamera);
        } else {
            Intent sendToSearch = new Intent(LoginActivity.this, SearchActivity.class);
            sendToSearch.putExtra("action", "login");
            startActivity(sendToSearch);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
