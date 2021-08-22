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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button  cameraButton;

    private final static int    REQUEST_IMAGE_CAPTURE   =   124;

    FirebaseVisionImage image;
    FirebaseVisionFaceDetector  detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        cameraButton    =   findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent  =   new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) !=  null)   {
                    startActivityForResult(intent,  REQUEST_IMAGE_CAPTURE);
                }   else {
                    Toast.makeText(MainActivity.this,   "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  REQUEST_IMAGE_CAPTURE   &&  resultCode  ==  RESULT_OK)  {
            Bundle  extra   =   data.getExtras();
            Bitmap  bitmap  =   (Bitmap)    extra.get("data");
            detectFace(bitmap);
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
                            resultText  =   resultText.concat("\nFACE NUMBER. " +   i   +   ": ")
                                    .concat("\nSmile: " +   face.getSmilingProbability()    *100    +   "%")
                                    .concat("\nleft eye open: " +   face.getLeftEyeOpenProbability()    *100    +   "%")
                                    .concat("\nright eye open: "  +   face.getRightEyeOpenProbability()   *100    +   "%");
                            i++;
                        }

                        if (firebaseVisionFaces.size()  ==  0)  {
                            Toast.makeText(MainActivity.this,   "No Face Detected", Toast.LENGTH_LONG).show();
                        }   else {
                            Bundle  bundle  =   new Bundle();
                            bundle.putString(LCOFaceDetection.RESULT_TEXT,  resultText);
                            DialogFragment  resultDialog  =   new ResultDialog();
                            resultDialog.setArguments(bundle);
                            resultDialog.setCancelable(true);
                            resultDialog.show(getSupportFragmentManager(),  LCOFaceDetection.RESULT_DIALOG);
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