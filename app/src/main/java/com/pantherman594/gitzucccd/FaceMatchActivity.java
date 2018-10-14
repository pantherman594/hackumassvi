package com.pantherman594.gitzucccd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FaceMatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_match);

        Intent intent = getIntent();
        Bitmap target = intent.getParcelableExtra("target");

        String[] usernames = new String[Friend.size()];
        Bitmap[] sourceBmps = new Bitmap[Friend.size()];

        for (int i = 0, size = Friend.size(); i < size; i++) {
            Friend friend = Friend.getFriend(i);

            usernames[i] = friend.getUsername();
            sourceBmps[i] = friend.getProfImg();
        }

        if (OpenCVLoader.initDebug()) {
            Pair<String, Double> result = compare(usernames, sourceBmps, target);

            // Send the closest match to ResultActivity
            Intent sendToResult = new Intent(FaceMatchActivity.this, ResultActivity.class);
            sendToResult.putExtra("username", result.first);
            sendToResult.putExtra("confidence", result.second);
            startActivity(sendToResult);
        } else {
            Log.e("Error", "OpenCV libraries are not loaded!");
        }
    }

    // Processes bitmaps for facial recognition
    private Mat toGrayMat(Bitmap input) {
        Mat output = new Mat();

        // Convert Bitmap to Mat
        input = input.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(input, output);

        // Convert to grayscale
        Imgproc.cvtColor(output, output, Imgproc.COLOR_RGB2GRAY, 4);


        return output;
    }

    // Compare the source images against the target
    private Pair<String, Double> compare(String[] usernames, Bitmap[] sourceBmps, Bitmap targetBmp) {
        // Convert source and target bitmaps into Mat
        Mat target = toGrayMat(targetBmp);

        FaceRecognizer model = LBPHFaceRecognizer.create();

        double smallestConfidence = 999;
        int smallestConfidenceIndex = 0;

        // Loop through the source bitmaps and train the model with them
        for (int i = 0, len = sourceBmps.length; i < len; i++) {
            Bitmap sourceBmp = sourceBmps[i];
            Mat source = toGrayMat(sourceBmp);

            List<Mat> src = new ArrayList<>();
            src.add(source);

            // Create an array of size (cols) 1 and scalar value i
            Mat labels = new Mat(1, 1, CvType.CV_32SC1, new Scalar(0));
            // Train the model with the source image
            model.train(src, labels);

            // Create variables for model.predict to store into
            int[] predictedLabel = new int[1];
            double[] confidence = new double[1];

            model.predict(target, predictedLabel, confidence);

            if (confidence[0] < smallestConfidence) {
                smallestConfidence = confidence[0];
                smallestConfidenceIndex = i;
            }
        }

        return new Pair<>(usernames[smallestConfidenceIndex], smallestConfidence);
    }
}
