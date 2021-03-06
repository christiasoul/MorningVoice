package edu.cogswell.morningvoice;

import android.app.AlarmManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TimePicker;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private static TextToSpeech textReader;
    private Button startBut;
    private static String [][] readInfo;
    private static int secReadNum;
    private static final int readLength = 10;
    private static final int readTypeNum = 3;
    private static int curReadStop;
    private static int [] readStopAry;
    private static WeatherInfo myWeather;
    private ExpandableListAdapter myAdapter;
    private ExpandableListView myExpand;
    private static int curWriteSection;

    // Location
    private static LocationManager locManager;
    private static Location myLoc;
    private static int MY_PERMISSION_ACCESS_COURSE_LOCATION = 358;

    // Alarm things
    private static AlarmManager timeControl;
    private TimePicker myPicker;
    private static Button [] dayButtons;
    private AlarmInfo myAlarm;


    public enum  itemReadType{Weather, File, Reddit }
    private static itemReadType [] curReadList; // Contains types have been read in the current read

    private static SaveState curSaveState = new SaveState();

    private static long timeCheckedWeather;
    private final static long minTimeUntilWeatherReset = 240 * 60 * 1000;

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


        System.out.printf(getFilesDir().getPath());
        System.out.printf("\n\nStarting text to speech\n");

        textReader =  new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if (status != TextToSpeech.ERROR){
                    textReader.setLanguage(Locale.getDefault());
                }else {
                    // Give error dialog box
                    System.out.printf("Could not find language");
                }
            }
        });

        curWriteSection = 0;

        startBut = (Button) findViewById(R.id.run);
        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do button things
            }
        });

        readInfo = new String[readLength][10];
        curReadList = new itemReadType[readTypeNum];
        readStopAry = new int[readLength];
        curReadStop = 0;


        Log.d("D","Doing Location");
        locManager = (LocationManager) this.getSystemService(getBaseContext().LOCATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP ) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSION_ACCESS_COURSE_LOCATION);
            } else {
                myLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("D","Got Location");
            }
        }else{
            myLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d("D","Got Location");
        }

        if (myLoc == null) {System.out.printf("\nLocation is null!\n");}

        //Option things
        myPicker = (TimePicker)findViewById(R.id.timePicker);
        SeekBar volumeControl = (SeekBar) findViewById(R.id.volume);

        Button [] dayButtons = new Button[7];

        dayButtons[0] = (Button)findViewById(R.id.sun);
        dayButtons[1] = (Button)findViewById(R.id.mon);
        dayButtons[2] = (Button)findViewById(R.id.tue);
        dayButtons[3] = (Button)findViewById(R.id.wed);
        dayButtons[4] = (Button)findViewById(R.id.thu);
        dayButtons[5] = (Button)findViewById(R.id.fri);
        dayButtons[6] = (Button)findViewById(R.id.sat);

        Options.getInstance().setViews(dayButtons, volumeControl, getBaseContext(), myPicker);
        Options.getInstance().setOptions();
        if (Long.getLong(Options.getInstance().getTimeSinceLast()) != null){
            timeCheckedWeather = Long.parseLong(Options.getInstance().getTimeSinceLast());
        }else{
            timeCheckedWeather = 0;
        }


        // Alarm things

        System.out.printf("Doing weather\n");
        myWeather = new WeatherInfo(getBaseContext());

        // Setup for adapter
        String [] headerNames = {"Options", "Weather", "Reddit", "File"};
        String [] headerVal = {"Opt", "Wea", "Redd", "Fil"};
        String [][] childrenNames = {
                {},
                {"Reads Weather", "Reads City Name", "Reads Temperature", "Reads Humidity",
                    "Reads Pressure", "Reads Wind", "Reads Cloudiness", "Reads Visibility",
                    "Reads Precipitation", "Reads Sunset And Sunrise"},
                {"Reads Reddit"},
                {"Reads File"},
        };
        String[][] childrenVals =  {
                {},
                {"Wea", "Cit", "Tem", "Hum", "Pres", "Win", "Clo", "Vis", "Prec", "Sun"},
                {"Red"},
                {"Fil"},
        };

        System.out.printf("Doing expandable list\n");
        myExpand = (ExpandableListView)findViewById(R.id.expand_list);
        myAdapter = new ExpandableListAdapter(getApplicationContext(), headerNames,
                headerVal , childrenNames, childrenVals  , Options.getInstance().getBools() );
        myExpand.setAdapter(myAdapter);

        /* Voice things
        Set<Voice> voiceList = textReader.getVoices();
        for (Voice curVoice : voiceList){
            curVoice.
        }
        */

        dayButtons = new Button[7];
        dayButtons[0] = (Button)findViewById(R.id.sun);
        dayButtons[1] = (Button)findViewById(R.id.mon);
        dayButtons[2] = (Button)findViewById(R.id.tue);
        dayButtons[3] = (Button)findViewById(R.id.wed);
        dayButtons[4] = (Button)findViewById(R.id.thu);
        dayButtons[5] = (Button)findViewById(R.id.fri);
        dayButtons[6] = (Button)findViewById(R.id.sat);

        dayButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(0);
                Options.getInstance().setButtonsAll();
            }
        });
        dayButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(1);
                Options.getInstance().setButtonsAll();
            }
        });
        dayButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(2);
                Options.getInstance().setButtonsAll();
            }
        });
        dayButtons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(3);
                Options.getInstance().setButtonsAll();
            }
        });
        dayButtons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(4);
                Options.getInstance().setButtonsAll();
            }
        });
        dayButtons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(5);
                Options.getInstance().setButtonsAll();
            }
        });
        dayButtons[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Options.getInstance().setDay(6);
                Options.getInstance().setButtonsAll();
            }
        });

        startBut = (Button)findViewById(R.id.run);

        startBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.run();
            }
        });



        timeControl = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarm = new AlarmInfo(myPicker, timeControl, getApplicationContext());

        // Setting up savestate
        curSaveState = new SaveState();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        Options.getInstance().writeToFile(timeCheckedWeather);
    }

    public static void run(){

        System.out.printf("Running\n");
        // Making sure that enough time has passed since the last update to weather before updating again. Uses time since boot
        if (SystemClock.elapsedRealtime() > minTimeUntilWeatherReset + timeCheckedWeather ||
                timeCheckedWeather == 0 ){
            // Update
            if (Options.getInstance().getZipCode() == 0 || Options.getInstance().getCountryCode().contains("null")){
                if (myLoc != null){
                    System.out.printf("Updating Weather with location");
                    timeCheckedWeather = myWeather.update(myLoc);

                }else {
                    System.out.printf("prompt for zip code");
                    // Prompt user to input zip code and country code or turn off weather
                }
            }else{
                //
                System.out.printf("D","Updating Weather");
                timeCheckedWeather = myWeather.update();

            }

        }else{
            System.out.printf("You have summoned me too soon");
        }

        secReadNum = 0;
        //load();
        //read("Some Name");
        // while(read("Some Name"));
        //curSaveState.isCompleted();

        readWeather((byte)0);
        textReader.speak(readInfo[secReadNum][0].subSequence(0, readInfo[secReadNum][0].length()), TextToSpeech.QUEUE_ADD, null,
                "Weather");

    }

    /*
    private static boolean read(String refName){

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

    private static boolean load(){
        System.out.printf("Im loading!\n");
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

    private static boolean checkTypeAr(itemReadType[] ar, itemReadType item){
        for (int i = 0; i < ar.length; i++){

            if (ar[i] == item) {return true;}

        }
        return false;
    }

    private static byte getLoadFunction(byte checkNum, itemReadType setReadCheck){
        if (setReadCheck == itemReadType.Weather){
            checkNum = readWeather(checkNum);
        }else if (setReadCheck == itemReadType.Reddit){
            checkNum = readReddit(checkNum);
        }else if (setReadCheck == itemReadType.File){
            checkNum = readFile(checkNum);
        }

        return checkNum;
    }
    */

    private static byte readWeather(byte checkNum){
        //Need to be changed for
        readInfo[curWriteSection][checkNum++] = myWeather.getString();
        return checkNum;
    }

    private static byte readReddit(byte checkNum){

        return checkNum;
    }

    private static byte readFile(byte checkNum){

        return checkNum;
    }

    protected void getLocation(){


    }


}
