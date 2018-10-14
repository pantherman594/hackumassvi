package com.pantherman594.gitzucccd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

// NOTE: While this holds the code for SearchActivity, the WebView is actually run under GitZucccdApplication.
// The WebView is not tied to this Activity.
public class SearchActivity extends AppCompatActivity {

    private WebView browser;

    private static int num = 0;

    @JavascriptInterface
    public void processHTML(String html) throws UnsupportedEncodingException {
        if (html == null)
            return;
        // Parse the html and extract the profile pictures
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".img.profpic");

        for (Element elem : elements) {
            // Parse the profile picture's background style to get the thumbnail url
            String imgStyle = elem.attr("style");
            String background = imgStyle.split("; ")[0];
            String bgValue = background.split(":")[1];
            String imgUrlEncoded = bgValue.replaceAll("^.+url\\('", "").replaceAll("'\\).+$", "");
            String imgUrl = URLDecoder.decode(imgUrlEncoded.replace(" ", "").replace("\\", "%"), "UTF-8");
            String name = elem.attr("aria-label");

            // Get the profile url from the parent element
            Element parent = elem.parent();
            String profUrl = parent.attr("href");

            if (profUrl.length() < 5) continue;
            Friend friend = new Friend(profUrl.substring(1), name);
            num++;
            new DownloadImgOperation(imgUrl, friend, new Runnable() {
                @Override
                public void run() {
                    if (--num == 0) {
                        // Send the user to the camera after all the friends have been downloaded
                        Intent sendToCamera = new Intent(SearchActivity.this, CameraActivity.class);
                        startActivity(sendToCamera);
                    }
                }
            }).execute("");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView rotateImage= (ImageView) findViewById(R.id.rotate_image);
        Animation startRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.android_rotate_animation);
        rotateImage.startAnimation(startRotateAnimation);

        // Get the webview from the application
        // browser = findViewById(R.id.search_webview);
        browser = ((GitZucccdApplication) getApplication()).getBrowser();
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(this, "HTMLOUT");

        final WebViewClient loadFriendsClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!url.contains("friends")) return;

                // Inject JavaScript to go to the bottom of the page. If it goes no further, send it to processHTML().
                browser.loadUrl("javascript:" +
                    "function scrollBottom(oldHeight, callback, num = 0) {" +
                        "if (document.body.scrollHeight !== oldHeight || num < 6) {" +
                            "if (document.body.scrollHeight !== oldHeight) {" +
                                "num = 0;" +
                            "} else {" +
                                "num++;" +
                            "}" +
                            "var newHeight = document.body.scrollHeight;" +
                            "window.scrollTo(0, 999999999);" +
                            "setTimeout(function() {" +
                                "scrollBottom(newHeight, callback, num);" +
                            "}, 500);" +
                        "} else {" +
                            "callback();" +
                        "}" +
                    "}" +
                    "scrollBottom(0, function() {" +
                        // This sends the html to the processHTML function
                        "window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');" +
                    "});");
            }
        };

        final WebViewClient logInClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains("home.php")) return false;

                // Wait until the user is logged in, then load the friends list
                browser.setWebViewClient(loadFriendsClient);
                browser.loadUrl("https://m.facebook.com/me/friends");

                return false;
            }
        };

        browser.setWebViewClient(logInClient);
        browser.loadUrl("https://m.facebook.com/login.php");
    }

    private class DownloadImgOperation extends AsyncTask<String, Void, String> {
        private String url;
        private Friend friend;
        private Runnable callback;

        public DownloadImgOperation(String url, Friend friend, Runnable callback) {
            this.url = url;
            this.friend = friend;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                // Download the image
                URL dlUrl = new URL(url);

                HttpURLConnection connection = (HttpURLConnection) dlUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
                // Convert the image into a Bitmap
                Bitmap downloadedBmp = BitmapFactory.decodeStream(input);

                // Convert the bitmap to grayscale
                Bitmap grayscaleBmp = Bitmap.createBitmap(downloadedBmp.getWidth(), downloadedBmp.getHeight(), Bitmap.Config.RGB_565);
                Canvas c = new Canvas(grayscaleBmp);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);
                c.drawBitmap(downloadedBmp, 0, 0, paint);
                downloadedBmp.recycle();

                try {
                    // Use the compress method on the Bitmap object to write image the OutputStream
                    FileOutputStream fos = new FileOutputStream(new File(SearchActivity.this.getFilesDir(), friend.getUsername() + ".png"));

                    // Write the bitmap to the output stream
                    grayscaleBmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();

                    // Write other user data to a data file
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(SearchActivity.this.openFileOutput(friend.getUsername() + ".dat", Context.MODE_PRIVATE));
                    outputStreamWriter.write(friend.getName() + ";;;");
                    outputStreamWriter.write(friend.getUsername() + ";;;");
                    outputStreamWriter.write(String.valueOf(friend.isId()));
                    outputStreamWriter.close();
                } catch (Exception e) {
                }

                // Save the friend and add to the database
                friend.setProfImg(grayscaleBmp);
                Friend.addFriend(friend);
            } catch (IOException ignored) {
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            callback.run();
        }
    }
}
