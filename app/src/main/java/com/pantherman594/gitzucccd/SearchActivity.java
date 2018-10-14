package com.pantherman594.gitzucccd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.AccessToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// NOTE: While this holds the code for SearchActivity, the WebView is actually run under GitZucccdApplication.
// The WebView is not tied to this Activity.
public class SearchActivity extends AppCompatActivity {

    private WebView browser;
    private Deque<String> profUrls;

    private static int num = 0;

    @JavascriptInterface
    public void processHTML(String html) throws UnsupportedEncodingException {
        if (html == null)
            return;
        // Parse the html and extract the profile pictures
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".img.profpic");

        List<String> imgUrls = new ArrayList<>();
        profUrls = new ArrayDeque<>();

        for (Element elem : elements) {
            // Parse the profile picture's background style to get the thumbnail url
            String imgStyle = elem.attr("style");
            String background = imgStyle.split("; ")[0];
            String bgValue = background.split(":")[1];
            String imgUrlEncoded = bgValue.replaceAll("^.+url\\('", "").replaceAll("'\\).+$", "");
            String imgUrl = URLDecoder.decode(imgUrlEncoded.replace(" ", "").replace("\\", "%"), "UTF-8");
            imgUrls.add(imgUrl);
            String name = elem.attr("aria-label");
            Log.d("LAAAA", imgUrl);

            // Get the profile url from the parent element
            Element parent = elem.parent();
            String profUrl = parent.attr("href");

            if (profUrl.length() < 5) continue;
            profUrls.addLast(profUrl.substring(1));
            Friend friend = new Friend(profUrl.substring(1), name);
            num++;
            new DownloadImgOperation(imgUrl, friend, null, new Runnable() {
                @Override
                public void run() {
                    Log.d("NNNN", "" + num);
                    if (--num == 0) {
                        Log.d("COMPLETE", "COMPLETE");

                        Intent sendToCamera = new Intent(SearchActivity.this, CameraActivity.class);
                        startActivity(sendToCamera);
                    }
                }
            }).execute("");
            Log.d("LBBBB", profUrl);
        }

        for (String profUrl : profUrls) {
            Log.d("PROFFFFF", ">" + profUrl);
        }

        Log.d("SSStep", "1");
        final String nextProf = profUrls.pop();
        Log.d("SSStep", "2");

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                browser = ((GitZucccdApplication) getApplication()).getBrowser();
//                browser.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//                        Log.d("PROFF", "finished");
//                        Log.d("PROFF", nextProf);
//
//                        browser.loadUrl("javascript:window.HTMLOUT.processFriend('" + nextProf + "', '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//                    }
//                });
//                Log.d("SSStep", "3");
//
//                Log.d("PROFFu", nextProf);
//                Log.d("PROFFurl", Friend.getProfUrl(nextProf));
//                browser.loadUrl(Friend.getProfUrl(nextProf));
//            }
//        });
    }

    @JavascriptInterface
    public void processFriend(String username, String html) {
        if (html == null)
            return;
        Log.d("PROFF", username);
        // Parse the html and extract the profile pictures
        Document doc = Jsoup.parse(html);
        String profPicUrl = doc.select(".timeline a>div>i.img.profpic").first().parent().parent().attr("href");
        if (profPicUrl.startsWith("/photo.php")) {
            profPicUrl = profPicUrl.substring("/photo.php?fbid=".length()).replaceAll("&id=.+$", "");
            profPicUrl = "https://m.facebook.com/photo/view_full_size/?fbid=" + profPicUrl;

            final String profPicUrlFinal = profPicUrl;

            String name = doc.select("#cover-name-root h3").text();

            if (profUrls.isEmpty()) {
                Log.d("DDDDD", "SEND TO FRIENDS");
                Intent sendToFriends = new Intent(SearchActivity.this, FriendsActivity.class);
                startActivity(sendToFriends);
                return;
            }
            final String nextProf = profUrls.pop();

            final Friend friend = new Friend(username, name);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    browser.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                            new DownloadImgOperation(url, friend, nextProf, new Runnable() {
                                @Override
                                public void run() {
                                    processNext(nextProf);
                                }
                            }).execute("");

                            return false;
                        }
                    });

                    browser.loadUrl(profPicUrlFinal);
                }
            });
        } else {
            if (profUrls.isEmpty()) {
                Log.d("DDDDD", "SEND TO FRIENDS");
                Intent sendToFriends = new Intent(SearchActivity.this, FriendsActivity.class);
                startActivity(sendToFriends);
                return;
            }
            final String nextProf = profUrls.pop();
            processNext(nextProf);
        }
    }

    private void processNext(final String nextProf) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                browser.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        browser.loadUrl("javascript:window.HTMLOUT.processFriend('" + nextProf + "', '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    }
                });

                browser.loadUrl(Friend.getProfUrl(nextProf));
            }
        });
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d("BBBBBBBBB", "SearchActivity");
        // Get the webview from the application
        // browser = findViewById(R.id.search_webview);
        browser = ((GitZucccdApplication) getApplication()).getBrowser();
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(this, "HTMLOUT");

        final WebViewClient loadFriendsClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("BBBBBBBB", "Pre-INJECT");
                Log.d("BBBBBBBB", url);
                if (!url.contains("friends")) return;

                Log.d("BBBBBBBB", "INJECT");
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
                Log.d("BBBBBBBB", "Pre-INJECT A");
                Log.d("BBBBBBBBA", url);
                if (!url.contains("home.php")) return false;

                // Wait until the user is logged in, then load the friends list
                browser.setWebViewClient(loadFriendsClient);
                browser.loadUrl("https://m.facebook.com/me/friends");

                return false;
            }
        };

        browser.setWebViewClient(logInClient);
        browser.loadUrl("https://m.facebook.com/login.php");
        Log.d("BBBBBBBBBBBLLLLL", "Load");
    }

    private class DownloadImgOperation extends AsyncTask<String, Void, String> {
        private String url;
        private Friend friend;
        private String nextProf;
        private Runnable callback;

        public DownloadImgOperation(String url, Friend friend, String nextProf, Runnable callback) {
            this.url = url;
            this.friend = friend;
            this.nextProf = nextProf;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("UUUUUU", url);
            try {
                URL dlUrl = new URL(url);

                HttpURLConnection connection = (HttpURLConnection) dlUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream input = connection.getInputStream();
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
                    Log.d("WWWWWW", friend.getUsername());
                    // Use the compress method on the Bitmap object to write image to
                    // the OutputStream
                    FileOutputStream fos = SearchActivity.this.openFileOutput(friend.getUsername() + ".png", Context.MODE_PRIVATE);

                    // Writing the bitmap to the output stream
                    grayscaleBmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(SearchActivity.this.openFileOutput(friend.getUsername() + ".dat", Context.MODE_PRIVATE));
                    outputStreamWriter.write(friend.getName() + ";;;");
                    outputStreamWriter.write(friend.getUsername() + ";;;");
                    outputStreamWriter.write(String.valueOf(friend.isId()));
                    outputStreamWriter.close();
                } catch (Exception e) {
                }

                friend.setProfImg(grayscaleBmp);
                Friend.addFriend(friend);

                //processNext(nextProf);
            } catch (IOException ignored) {
                // TODO: show error with download
                //processNext(nextProf);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            callback.run();
        }
    }
}
