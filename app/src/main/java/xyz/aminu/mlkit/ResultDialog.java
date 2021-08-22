package xyz.aminu.mlkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {

    Button  okBtn,  recaptureButton;
    TextView    resultTextView;
    ImageView   displayImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View    view    =   inflater.inflate(R.layout.fragment_resultdialog,    container,  false);

        String  resultText  =   "";

        okBtn   =   view.findViewById(R.id.result_ok_button);
        recaptureButton =   view.findViewById(R.id.recapture);
        resultTextView  =   view.findViewById(R.id.result_text_view);
        displayImage    =   view.findViewById(R.id.displayImage);

        Bundle  bundle  =   getArguments();
        resultText  =   bundle.getString(LCOFaceDetection.RESULT_TEXT);
        resultTextView.setText(resultText);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        recaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
