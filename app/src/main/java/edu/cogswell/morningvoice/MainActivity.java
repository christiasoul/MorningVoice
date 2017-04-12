package edu.cogswell.morningvoice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textReader;
    private Button startBut;
    private String [][] readInfo;
    private int secReadNum;
    private final int readLength = 10;
    private final int readTypeNum = 3;
    private int curReadStop;
    private int [] readStopAry;

    // Alarm things
    private AlarmManager timeControl;
    private PendingIntent timeIntent;
    private Calendar curCalendar;


    public enum  itemReadType{Weather, File, Reddit }
    private itemReadType [] curReadList; // Contains types have been read in the current read

    private SaveState curSaveState = new SaveState();

    private int timeSinceCheckedWeather;
    private final int minTimeUntilWeatherReset = 240;

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

        readInfo = new String[readLength][10];
        curReadList = new itemReadType[readTypeNum];
        readStopAry = new int[readLength];

        // Alarm things
        curCalendar = Calendar.getInstance();
        curCalendar.setTimeInMillis(System.currentTimeMillis());

        timeControl = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent curIntent = new Intent(this, MyAlarmReceiver.class);
        timeIntent = PendingIntent.getBroadcast(this, 0, curIntent, 0);

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);



        /* Voice things
        Set<Voice> voiceList = textReader.getVoices();
        for (Voice curVoice : voiceList){
            curVoice.
        }
        */

    }

    protected void run(){

        Options.getInstance().setOptions();
        load();
        while (read("Some Name"));

    }

    protected boolean read(String refName){

        int erCheck;
        if (textReader.isSpeaking())
        for (int i = 0; i < readInfo[secReadNum].length; i++) {
            erCheck = textReader.speak(readInfo[secReadNum][i].subSequence(0,
                    readInfo[secReadNum][i].length()),  TextToSpeech.QUEUE_ADD, null,
                    refName + Integer.toString(i));

            if (erCheck == TextToSpeech.ERROR){
                // Do error code
            }
        }

        if (++secReadNum == curReadStop){

            return load();
        }else {
            return true;
        }
    }

    protected boolean load(){
        byte typeCheck = 0;
        byte strCheck = 0;
        Options curOptions = Options.getInstance();
        itemReadType curTypeCheck;
        if (curSaveState.isCompleted()){
            curTypeCheck = curOptions.getReadOrder()[typeCheck];
        }else {
            curTypeCheck = curSaveState.getSaveType();
            strCheck = getLoadFunction(strCheck, curTypeCheck);
            if (!curSaveState.isCompleted() || strCheck >= readLength - (readLength /4)){
                return true;
            }else{
                curTypeCheck = curOptions.getReadOrder()[typeCheck];
            }
        }

        while (typeCheck < curOptions.getReadLen()  && strCheck < readLength){
            if (checkTypeAr(curReadList, curTypeCheck)){
                strCheck = getLoadFunction(strCheck, curTypeCheck);
            }else{
                curTypeCheck = curOptions.getReadOrder()[++typeCheck];
            }

        }

        if (strCheck == 0){
            // Empty curReadList
            return false;

        }else {
            secReadNum = strCheck;
            return true;
        }

    }

    protected boolean checkTypeAr(itemReadType[] ar, itemReadType item){
        for (int i = 0; i < ar.length; i++){

            if (ar[i] == item) {return true;}

        }
        return false;
    }

    protected byte getLoadFunction(byte checkNum, itemReadType setReadCheck){
        if (setReadCheck == itemReadType.Weather){
            checkNum = readWeather(checkNum);
        }else if (setReadCheck == itemReadType.Reddit){
            checkNum = readReddit(checkNum);
        }else if (setReadCheck == itemReadType.File){
            checkNum = readFile(checkNum);
        }

        return checkNum;
    }

    protected byte readWeather(byte checkNum){

        return checkNum;
    }

    protected byte readReddit(byte checkNum){

        return checkNum;
    }

    protected byte readFile(byte checkNum){

        return checkNum;
    }

}
