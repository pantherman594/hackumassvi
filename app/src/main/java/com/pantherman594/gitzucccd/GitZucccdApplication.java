package com.pantherman594.gitzucccd;

import android.app.Application;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class GitZucccdApplication extends Application {
    private WebView browser;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the webview in the application so that it can persist across activities
        browser = new WebView(this);
        browser.setId(R.id.search_webview);
        browser.setLayoutParams(new LinearLayout.LayoutParams(1080, 1920));
    }

    public WebView getBrowser() {
        return browser;
    }
}
