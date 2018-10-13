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
        String action = intent.getStringExtra("action");
        if (!action.equals("wait")) {
            sendToSearch();
        }
    }

    private void sendToSearch() {
        Intent sendToSearch = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(sendToSearch);
    }
}
