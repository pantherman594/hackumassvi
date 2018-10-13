package com.pantherman594.gitzucccd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity {

    // Put the camera here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        AccessToken token = intent.getParcelableExtra("token");
        sendToSearch(token);
    }

    private void sendToSearch(AccessToken token) {
        Intent sendToMain = new Intent(MainActivity.this, SearchActivity.class);
        sendToMain.putExtra("token", token);
        startActivity(sendToMain);
    }
}
