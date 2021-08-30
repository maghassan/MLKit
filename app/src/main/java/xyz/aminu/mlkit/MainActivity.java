package xyz.aminu.mlkit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button  cameraButton;

    private final static int    REQUEST_IMAGE_CAPTURE   =   124;

    FirebaseVisionImage image;
    FirebaseVisionFaceDetector  detector;

    Bitmap  bitmap;

    private ImageView   displayImage;
    private TextView    leftEye,    rightEye,    result_text_view,   face_recognition_text,  smiling_prob;

    float   finalImageProb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        leftEye =   findViewById(R.id.leftEyeBlink);
        rightEye    =   findViewById(R.id.rightEyeBlink);
        result_text_view    =   findViewById(R.id.result_text_view);
        face_recognition_text   =   findViewById(R.id.face_recognition_text);
        smiling_prob    =   findViewById(R.id.smiling_prob_text);

        cameraButton    =   findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        displayImage    =   findViewById(R.id.displayImage);
    }

    private void startCamera() {

        Intent  intent  =   new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) !=  null)   {
            startActivityForResult(intent,  REQUEST_IMAGE_CAPTURE);
        }   else {
            Toast.makeText(MainActivity.this,   "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  REQUEST_IMAGE_CAPTURE   &&  resultCode  ==  RESULT_OK)  {
            Bundle  extra   =   data.getExtras();
            bitmap  =   (Bitmap)    extra.get("data");
            detectFace(bitmap);
            displayImage.setImageBitmap(bitmap);
        }
    }

    private void detectFace(Bitmap bitmap) {

        FirebaseVisionFaceDetectorOptions   options =   new FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        try {
            image   =   FirebaseVisionImage.fromBitmap(bitmap);
            detector    = FirebaseVision.getInstance().getVisionFaceDetector(options);
        }   catch (Exception    e)  {
            e.printStackTrace();
        }

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        String  resultText  =   "";
                        int i   =   1;
                        for (FirebaseVisionFace face    :   firebaseVisionFaces)    {
                            resultText  =   resultText.concat("\nFACE NUMBER: " +   i   +   ": ")
                                    .concat("\nSmile: " +   face.getSmilingProbability()    *100    +   "%")
                                    .concat("\nleft eye open: " +   face.getLeftEyeOpenProbability()    *100    +   "%")
                                    .concat("\nright eye open: "  +   face.getRightEyeOpenProbability()   *100    +   "%");
                            i++;

                            float   leftEyeProb =   face.getLeftEyeOpenProbability()    *100;
                            float   rightEyeProb    =   face.getRightEyeOpenProbability()   *100;
                            float   smilingProb =   face.getSmilingProbability()    *100;

                            if (leftEyeProb    <=   90) {
                                leftEye.setText("Please open blink your left eye: "   +face.getLeftEyeOpenProbability()   *100    +   "%");
                                leftEye.setTextColor(getResources().getColor(R.color.red));
                            }   else    if (leftEyeProb >=90){
                                leftEye.setText("Left eye correct");
                                leftEye.setTextColor(getResources().getColor(R.color.green));
                            }

                            if (rightEyeProb    <=   90) {
                                rightEye.setText("Please open blink your right eye: "   +face.getLeftEyeOpenProbability()   *100    +   "%");
                                rightEye.setTextColor(getResources().getColor(R.color.red));
                            }   else    if (leftEyeProb >=90){
                                rightEye.setText("Right eye correct");
                                rightEye.setTextColor(getResources().getColor(R.color.green));
                            }

                            if (smilingProb <=  50)   {
                                smiling_prob.setText("Please smile");
                                smiling_prob.setTextColor(getResources().getColor(R.color.red));
                            }   else if (smilingProb    >=  50) {
                                smiling_prob.setText("Beautiful Smile");
                                smiling_prob.setTextColor(getResources().getColor(R.color.green));
                            }

                            /*if (face.getLeftEyeOpenProbability()    !=  FirebaseVisionFace.UNCOMPUTED_PROBABILITY){
                                float   finalEyeProb =   face.getLeftEyeOpenProbability();
                                finalImageProb  = eyeProb *100;
                                String  prob    =   "";
                                *//*if (finalEyeProb    <   90) {
                                    *//**//*leftEye.setText("Please blink your left eye: "  +   prob);
                                    leftEye.setTextColor(880808);*//**//*

                                }*//*
                            }   else    if (finalImageProb  <   90){
                                resultText  =   resultText.concat("\nPlease open your eyes" +   face.getLeftEyeOpenProbability()    *100    +   "%");
                            }*/
                        }

                        if (firebaseVisionFaces.size()  ==  0)  {
                            Toast.makeText(MainActivity.this,   "No Face Detected", Toast.LENGTH_LONG).show();
                            cameraButton.setText("Re Capture");
                            face_recognition_text.setText("No Face Detected");
                            face_recognition_text.setTextColor(getResources().getColor(R.color.red));
                        }   else    if (firebaseVisionFaces.size()  >   0){
                            face_recognition_text.setText("Face Detected");
                            face_recognition_text.setTextColor(getResources().getColor(R.color.green));
                        }

                        else {
                            Bundle  bundle  =   new Bundle();
                            bundle.putString(LCOFaceDetection.RESULT_TEXT,  resultText);
                            DialogFragment  resultDialog  =   new ResultDialog();
                            resultDialog.setArguments(bundle);
                            resultDialog.setCancelable(true);
                            resultDialog.show(getSupportFragmentManager(),  LCOFaceDetection.RESULT_DIALOG);

                            //result_text_view.setText(LCOFaceDetection.RESULT_TEXT, TextView.BufferType.valueOf(resultText));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,   "Oops, Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}