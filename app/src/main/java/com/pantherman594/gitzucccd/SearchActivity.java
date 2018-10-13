package com.pantherman594.gitzucccd;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        AccessToken token = intent.getParcelableExtra("token");

        String userId = token.getUserId();
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
