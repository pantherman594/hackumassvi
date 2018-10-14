package com.pantherman594.gitzucccd;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get the username and confidence from FaceMatchActivity, and display it on screen
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        double confidence = intent.getDoubleExtra("confidence", 0.0);

        RelativeLayout resultWrapper = findViewById(R.id.result_wrapper);
        ImageView resultImage = findViewById(R.id.result_image);
        TextView resultName = findViewById(R.id.result_name);
        TextView resultConfidence = findViewById(R.id.result_confidence);

        final Friend friend = Friend.getFriend(username);
        resultWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(friend.getProfUrl()));
                startActivity(browserIntent);
            }
        });
        resultImage.setImageBitmap(friend.getProfImg());
        resultName.setText(friend.getName());
        resultConfidence.setText(String.valueOf(confidence));
    }
}
