package edu.cogswell.morningvoice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {


    private TextToSpeech textReader;
    private Button startBut;
    private String [][] readInfo;
    private int secReadNum;
    private final int readLength = 10;
    private final int readTypeNum = 3;
    private int curReadStop;
    private int [] readStopAry;
    private WeatherInfo myWeather;
    private ExpandableListAdapter myAdapter;
    private LocationManager locManager;

    // Alarm things
    private AlarmManager timeControl;
    private PendingIntent timeIntent;
    private Calendar curCalendar;


    public enum  itemReadType{Weather, File, Reddit }
    private itemReadType [] curReadList; // Contains types have been read in the current read

    private SaveState curSaveState = new SaveState();

    private long timeCheckedWeather;
    private final long minTimeUntilWeatherReset = 240 * 60 * 1000;

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    */

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


        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if ( ContextCompat.checkSelfPermission( this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSION_ACCESS_COURSE_LOCATION);
        }else {
            Location myLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // Alarm things
        curCalendar = Calendar.getInstance();
        curCalendar.setTimeInMillis(System.currentTimeMillis());

        timeControl = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent curIntent = new Intent(this, MyAlarmReceiver.class);
        timeIntent = PendingIntent.getBroadcast(this, 0, curIntent, 0);

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        myWeather = new WeatherInfo();

        // Setup for adapter
        String [] headerNames = {"Options", "Weather", "Reddit", "File"};
        String [] headerVal = {"Opt", "Wea", "Redd", "Fil"};
        String [][] childrenNames = {
                {},
                {"Reads Weather", "Reads City Name", "Reads Temperature", "Reads Humidity",
                    "Reads Pressure", "Reads Wind", "Reads Cloudiness", "Reads Visibility",
                    "Reads Precipitation", "Reads Sunset And Sunrise"},
                {"Reads Reddit"},
                {"Reads File"}
        };
        String[][] childrenVals =  {
                {},
                {"Wea", "Cit", "Tem", "Hum", "Pres", "Win", "Clo", "Vis", "Prec", "Sun"},
                {"Red"},
                {"Fil"}
        };

        myAdapter = new ExpandableListAdapter(getApplicationContext(), headerNames,
                headerVal , childrenNames, childrenVals  , Options.getInstance().getBools() );

        /* Voice things
        Set<Voice> voiceList = textReader.getVoices();
        for (Voice curVoice : voiceList){
            curVoice.
        }
        */




    }

    protected void run(){

        // fix, make time secure
        if (SystemClock.elapsedRealtime() > minTimeUntilWeatherReset + timeSinceCheckedWeather){
            timeCheckedWeather = myWeather.update(locManager);
        }

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

    protected void getLocation(){


    }


}
