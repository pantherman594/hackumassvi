package com.pantherman594.gitzucccd;

import android.app.Application;
import android.webkit.WebView;
import android.widget.LinearLayout;

import org.opencv.android.OpenCVLoader;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;

public class GitZucccdApplication extends Application {

    private WebView browser;
    private FaceRecognizer model;

    @Override
    public void onCreate() {
        super.onCreate();
        // Create the webview in the application so that it can persist across activities
        browser = new WebView(this);
        browser.setLayoutParams(new LinearLayout.LayoutParams(1080, 1920));

        // Initialize the FaceRecognizer here so that the model persists across mnavigation
        if (OpenCVLoader.initDebug()) {
            model = LBPHFaceRecognizer.create();
        }
    }

    public WebView getBrowser() {
        return browser;
    }

    public FaceRecognizer getModel() {
        return model;
    }
}
