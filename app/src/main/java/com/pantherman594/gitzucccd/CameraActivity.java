package com.pantherman594.gitzucccd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static com.pantherman594.gitzucccd.CameraActivity.CAM_REQUEST;

public class CameraActivity extends AppCompatActivity {

    Button btnpic;
    ImageView imgTakenPic;
    public static final int CAM_REQUEST=1313;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Log.d("myTag2", "this should run after loading camera activity");
        btnpic = (Button) findViewById(R.id.button);
        imgTakenPic = (ImageView)findViewById(R.id.imageView);
        btnpic.setOnClickListener(new btnTakePhotoClicker());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAM_REQUEST){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgTakenPic.setImageBitmap(bitmap);
        }
    }

    class btnTakePhotoClicker implements Button.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture,CAM_REQUEST);
        }
    }

}
