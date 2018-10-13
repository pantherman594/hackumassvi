package com.pantherman594.gitzucccd;

import android.annotation.TargetApi;
import android.content.Intent;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {

    @JavascriptInterface
    public void processHTML(String html) throws UnsupportedEncodingException {
        if (html == null)
            return;
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".img.profpic");

        List<String> imgUrls = new ArrayList<>();
        List<String> profUrls = new ArrayList<>();
        for (Element elem : elements) {
            String imgStyle = elem.attr("style");
            String background = imgStyle.split("; ")[0];
            String bgValue = background.split(":")[1];
            String imgUrlEncoded = bgValue.replaceAll("^.+url\\('", "").replaceAll("'\\).+$", "");
            String imgUrl = URLDecoder.decode(imgUrlEncoded.replace(" ", "").replace("\\", "%"), "UTF-8");
            imgUrls.add(imgUrl);
            Log.d("LAAAA", imgUrl);

            Element parent = elem.parent();
            profUrls.add(parent.attr("href"));
            Log.d("LBBBB", parent.attr("href"));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        AccessToken token = intent.getParcelableExtra("token");

        String userId = token.getUserId();

        final WebView browser = (WebView) findViewById(R.id.search_webview);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(this, "HTMLOUT");

        final WebViewClient loadFriendsClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!url.contains("friends")) return;

                browser.loadUrl("javascript:function scrollBottom(oldHeight, callback) {if (document.body.scrollHeight !== oldHeight) { var newHeight = document.body.scrollHeight; console.log(newHeight); window.scrollTo(0, 999999999); setTimeout(function() {scrollBottom(newHeight, callback);}, 3000); } else { callback(); } } scrollBottom(0, function() { window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); });");
            }
        };

        final WebViewClient logInClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!url.contains("home.php")) return;

                Toast.makeText(getApplicationContext(), "Logged in!",
                        Toast.LENGTH_LONG).show();
                browser.setWebViewClient(loadFriendsClient);
                browser.loadUrl("https://m.facebook.com/me/friends");
            }
        };

        browser.setWebViewClient(logInClient);
        browser.loadUrl("https://m.facebook.com/login.php");

//        Log.d("USERID", userId);
//        GraphRequest request = GraphRequest.newMyFriendsRequest(
//                token,
//                new GraphRequest.GraphJSONArrayCallback() {
//                    @Override
//                    public void onCompleted(
//                           JSONArray array,
//                           GraphResponse response) {
//                        Log.d("RESULT", array.toString());
//                        // Application code
//                    }
//                });
//        request.executeAsync();
    }

}
