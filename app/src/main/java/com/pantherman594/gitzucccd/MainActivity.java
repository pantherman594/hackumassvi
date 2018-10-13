package com.pantherman594.gitzucccd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    // Put the camera here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        AccessToken token = intent.getParcelableExtra("token");
        //sendToSearch(token);
        Log.d("myTag", "this should run before CameraActivity");
        cameraActivity();
    }

    private void cameraActivity() {
        Intent takePictureIntent = new Intent(MainActivity.this, CameraActivity.class);
      //  if (true || takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("ugh", "why u no work");
            startActivity(takePictureIntent);
        //}

    }


    private void sendToSearch(AccessToken token) {
        Intent sendToMain = new Intent(MainActivity.this, SearchActivity.class);
        sendToMain.putExtra("token", token);
        startActivity(sendToMain);
    }
}
