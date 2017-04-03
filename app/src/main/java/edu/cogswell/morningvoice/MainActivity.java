package edu.cogswell.morningvoice;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textReader;
    private Button startBut;
    private String [][] readInfo;
    private int secReadNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textReader =  new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if (status != TextToSpeech.ERROR){
                    textReader.setLanguage(Locale.getDefault());
                }else {
                    // Give error dialog box

                }
            }
        });

        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do button things
            }
        });



    }

    protected void read(String refName){
        int erCheck;
        for (int i = 0; i < readInfo[secReadNum].length; i++) {
            erCheck = textReader.speak(readInfo[secReadNum][i].subSequence(0,
                    readInfo[secReadNum][i].length()),  TextToSpeech.QUEUE_ADD, null,
                    refName + Integer.toString(i));

            if (erCheck == TextToSpeech.ERROR){
                // Do error code
            }
        }

    }
}
