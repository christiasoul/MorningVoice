package edu.cogswell.morningvoice;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TimePicker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Christian on 4/6/2017.
 */

public class Options extends Activity {
    private static final Options ourInstance = new Options();

    private String fileName = "/src/assets/optionscheck.xml";

    private AssetManager myAssets;

    private MainActivity.itemReadType [] readOrder;
    private byte readLen;
    private Context myContext;

    //Voice
    //private byte voiceType;
    private int voiceSpeed;

    private int volume;
    TimePicker myTimePicker;
    //Weather
    private boolean doesWeather;

    private boolean metricSystem;

    private boolean saysCity;
    private boolean saysWind;
    private boolean saysCloudy;
    private boolean saysPressure;
    private boolean saysHumidity;
    private boolean saysSunsetRise;
    private boolean saysCurTemp;
    private boolean saysPrecipitation;
    private boolean saysVisibility;

    private boolean readsReddit;

    private boolean readsFile;

    private int zipCode;
    private String countryCode;

    private String alarmTime;
    private boolean [] days = new boolean[7];

    public static Options getInstance() {
        return ourInstance;
    }

    private Options() {

    }

    //public byte getVoiceType() {        return voiceType;    }
    public int getVolume() {        return volume;    }
    public boolean isSaysCity(){    return saysCity;    }
    public boolean isMetricSystem(){    return metricSystem; }
    public boolean isDoesWeather() {        return doesWeather;    }
    public boolean isSaysWind() {        return saysWind;    }
    public boolean isSaysCloudy() {        return saysCloudy;    }
    public boolean isSaysPressure() {        return saysPressure;    }
    public boolean isSaysHumidity() {        return saysHumidity;    }
    public boolean isSaysSunsetRise() {        return saysSunsetRise;    }
    public boolean isSaysCurTemp() {        return saysCurTemp;    }
    public boolean isSaysPrecipitation() {        return saysPrecipitation;    }
    public boolean isSaysVisibility(){  return saysVisibility;   }
    public boolean isReadsReddit() {        return readsReddit;    }
    public boolean isReadFile() {        return readsFile;    }
    public MainActivity.itemReadType [] getReadOrder(){ return readOrder;   }
    public byte getReadLen() {  return readLen;  }
    public int getZipCode(){    return zipCode; }
    public String getCountryCode(){ return countryCode; }
    public String getAlarmTime() {  return alarmTime;    }
    public boolean [] getDays() {   return days;    }
    public void setDay(int dayNum) {   days[dayNum] ^= true; }

    private SeekBar volumeControl;
    private Button [] dayButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }



    public void writeToFile(long timeSince){
        try{
            InputStream optionChecks = getResources().openRawResource(R.raw.optionscheck);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document boolDoc = dBuilder.parse(optionChecks);

            boolDoc.getDocumentElement().normalize();

            Element root = boolDoc.getDocumentElement();

            NodeList weatherOp = root.getElementsByTagName("Weather");

            String [] headerList = {"Options", "Weather", "Reddit", "File", "Alarm"};
            String [][] childList = {
                    {"city", "time", "zip", "country", "volume"},
                    {"reads", "city", "temperature", "humidity",
                            "pressure", "wind", "clouds", "visibility", "precipitation", "sun"},
                    {"reads"},
                    {"reads"},
                    {"sun", "mon", "tue", "wed", "thu", "fri", "sat", "time"}
            };

            (root.getElementsByTagName(headerList[0])).item(0)
                    .getAttributes().getNamedItem(childList[0][1]).setNodeValue(Long.toString(timeSince));
            if (zipCode != 0) {
                (root.getElementsByTagName(headerList[0])).item(0)
                        .getAttributes().getNamedItem(childList[0][2]).setNodeValue(Integer.toString(zipCode));
            }
            if (countryCode != null) {
                (root.getElementsByTagName(headerList[0])).item(0)
                        .getAttributes().getNamedItem(childList[0][3]).setNodeValue(countryCode);
            }
            (root.getElementsByTagName(headerList[0])).item(0)
                    .getAttributes().getNamedItem(childList[0][4]).setNodeValue(Integer.toString(volume));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][0]).setNodeValue(Boolean.toString(doesWeather));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][1]).setNodeValue(Boolean.toString(saysCity));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][2]).setNodeValue(Boolean.toString(saysCurTemp));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][3]).setNodeValue(Boolean.toString(saysHumidity));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][4]).setNodeValue(Boolean.toString(saysPressure));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][5]).setNodeValue(Boolean.toString(saysWind));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][6]).setNodeValue(Boolean.toString(saysCloudy));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][7]).setNodeValue(Boolean.toString(saysVisibility));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][8]).setNodeValue(Boolean.toString(saysPrecipitation));
            (root.getElementsByTagName(headerList[1])).item(0)
                    .getAttributes().getNamedItem(childList[1][9]).setNodeValue(Boolean.toString(saysSunsetRise));
            (root.getElementsByTagName(headerList[2])).item(0)
                    .getAttributes().getNamedItem(childList[2][0]).setNodeValue(Boolean.toString(readsReddit));
            (root.getElementsByTagName(headerList[3])).item(0)
                    .getAttributes().getNamedItem(childList[3][0]).setNodeValue(Boolean.toString(readsFile));
            for (int i = 0; i < 7; i++) {
                (root.getElementsByTagName(headerList[4])).item(0)
                        .getAttributes().getNamedItem(childList[4][i]).setNodeValue(Boolean.toString(days[i]));
            }


            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(myContext.getResources().openRawResourceFd(R.raw.optionscheck).createOutputStream());
            Source input = new DOMSource(boolDoc);
            xformer.transform(input, output);

        }catch(Exception ex){
            System.out.printf("Exiting on writeToFile()");
        }

    }

    public void writeToFile(Document xmlDoc){
        try {
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            Result output = new StreamResult(myContext.getResources().openRawResourceFd(R.raw.optionscheck).createOutputStream());
            Source input = new DOMSource(xmlDoc);
            xformer.transform(input, output);
        }catch(Exception ex){

        }
    }

    public void setButtonColor(Button targButton, boolean colored){
        if (colored) {
            targButton.setBackgroundTintList(ContextCompat.getColorStateList(
                    myContext, R.color.button_pressed ));
        }else{
            targButton.setBackgroundTintList(ContextCompat.getColorStateList(
                    myContext, R.color.button_not_pressed ));
        }
    }

    public void setButtonsAll(){


        for (int i = 0; i < 7; i++){
            setButtonColor(dayButtons[i], days[i]);
        }
    }

    public void setViews(Button [] inDayButtons, SeekBar inVolumeControl, Context inMyContext, TimePicker inTimePicker){
        volumeControl = inVolumeControl;
        dayButtons = inDayButtons;
        myContext = inMyContext;
        myTimePicker = inTimePicker;


        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int volume = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

    }

    public void setOptions(){

        System.out.printf("Doing option things");

        Context myContext = getBaseContext();


        //voice type/volume
        if (volumeControl != null){
            volume = volumeControl.getProgress();
        }

        // setup days in case
        for (int i = 0; i < 7; i++) {   days[i] = false;    }

        readOrder = new MainActivity.itemReadType[1];
        readOrder[0] = MainActivity.itemReadType.Weather;
        try{
            System.out.printf("Trying to open file\n");
            // XML File

            InputStream optionChecks = getResources().openRawResource(R.raw.optionscheck);
            System.out.printf("Got file");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document boolDoc = dBuilder.parse(optionChecks);
            boolDoc.getDocumentElement().normalize();
            Element root = boolDoc.getDocumentElement();


            NodeList weatherOp = root.getElementsByTagName("Weather");
            String [] weatherList = {"reads", "city", "temperature", "humidity",
                "pressure", "wind", "clouds", "visibility", "precipitation", "sun"};
            boolean [] weathBool = new boolean [weatherList.length];

            System.out.printf("Reading bools\n");
            System.out.printf(String.valueOf(weatherOp.getLength()));
            for (int i = 0; i < weatherOp.getLength(); i++){
                weathBool[i] = Boolean.getBoolean(((Element)weatherOp.item(0)).getAttribute(weatherList[i]));
            }
            System.out.printf("WeathBools read \n");
            doesWeather = weathBool[0];
            saysCity = weathBool[1];
            saysCurTemp = weathBool[2];
            saysHumidity = weathBool[3];
            saysPressure = weathBool[4];
            saysWind = weathBool[5];
            saysCloudy = weathBool[6];
            saysVisibility = weathBool[7];
            saysPrecipitation = weathBool[8];
            saysSunsetRise = weathBool[9];

            readsReddit = Boolean.getBoolean(((Element)root.getElementsByTagName("Reddit")
                    .item(0)).getAttribute("reads"));
            // Temporary
            readsReddit = Boolean.getBoolean(((Element)root.getElementsByTagName("File")
                    .item(0)).getAttribute("reads"));

            System.out.printf("reading other things");


            String [] optionList = {"city", "time", "zip", "country"};
            NodeList optionOp = root.getElementsByTagName("Options");

            if (((Element)optionOp.item(0)).getAttribute(optionList[2]).contentEquals("null")){
                zipCode = 0;
            }else {
                zipCode = Integer.getInteger(((Element) optionOp.item(0)).getAttribute(optionList[2]));
            }
            countryCode = ((Element)optionOp.item(0)).getAttribute(optionList[3]);

            System.out.printf("Doing alarm");

            NodeList alarmOp = root.getElementsByTagName("Alarm");
            String [] alarmList = {
                    "sun", "mon", "tue", "wed", "thu", "fri", "sat", "time"
            };

            for (int i = 0; i < 7; i++) {
                days[i] = Boolean.getBoolean(((Element)alarmOp.item(0)).getAttribute(alarmList[i]));
            }

            System.out.printf("did file things");

            setButtonsAll();

            alarmTime = ((Element)alarmOp.item(0)).getAttribute(alarmList[7]);
            String [] tempTimes = alarmTime.split(":");
            myTimePicker.setCurrentHour(Integer.parseInt(tempTimes[0]));
            myTimePicker.setCurrentMinute(Integer.parseInt(tempTimes[1]));


        }catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.out.printf("Could not find the file item in options");
        }catch(Exception e){
            e.printStackTrace();
            System.out.printf("Some error happened in setOptions");
        }

        // Set order
        /*
        doesWeather = ((CheckBox)findViewById(R.id.doesWeather)).isChecked();
        metricSystem = ((CheckBox)findViewById(R.id.metricSystem)).isChecked();
        saysWind = ((CheckBox)findViewById(R.id.saysWind)).isChecked();
        saysCloudy = ((CheckBox)findViewById(R.id.saysCloudy)).isChecked();
        saysPressure = ((CheckBox)findViewById(R.id.saysPressure)).isChecked();
        saysHumidity = ((CheckBox)findViewById(R.id.saysHumidity)).isChecked();
        saysSunsetRise = ((CheckBox)findViewById(R.id.saysSunsetRise)).isChecked();
        saysHighLowTemp = ((CheckBox)findViewById(R.id.saysHighLowTemp)).isChecked();
        saysCurTemp = ((CheckBox)findViewById(R.id.saysCurTemp)).isChecked();
        saysPrecipitation = ((CheckBox)findViewById(R.id.saysPrecipitation)).isChecked();
        //saysUVIndex = ((CheckBox)findViewById(R.id.saysUVIndex)).isChecked();
        //saysAirPollution = ((CheckBox)findViewById(R.id.saysAirPollution)).isChecked();

        readsReddit = ((CheckBox)findViewById(R.id.readsReddit)).isChecked();

        readsFile = ((CheckBox)findViewById(R.id.readsFile)).isChecked();
        */

        readLen = 0;
        if (readsReddit) readLen++;
        if (doesWeather) readLen++;
        if (readsFile) readLen++;

        // !!! hard coded to use only weather

        doesWeather = true;
        readLen = 1;

    }

    public boolean [][] getBools(){
        boolean [][] retBool = {
                {},
                {doesWeather, saysCity, saysCurTemp, saysHumidity, saysPressure, saysWind,
                    saysCloudy, saysVisibility, saysPrecipitation, saysSunsetRise},
                {readsReddit},
                {readsFile}
        };
        return retBool;
    }

    public void setBool(int head, int child, boolean state){
        if (head == 1){
            if (child == 0){    doesWeather = state;    }
            else if (child == 1){   saysCity = state;   }
            else if (child == 2){   saysCurTemp = state;}
            else if (child == 3){   saysHumidity = state;}
            else if (child == 4){   saysPressure = state;}
            else if (child == 5){   saysWind = state;}
            else if (child == 6){   saysCloudy = state;}
            else if (child == 7){   saysVisibility = state;}
            else if (child == 8){   saysPrecipitation = state;}
            else if (child == 9){   saysSunsetRise = state;}
        }
        else if (head == 2){
            if (child == 0){    readsReddit = state;    }
        }
        else if (head == 3){
            if (child == 0){    readsReddit = state;    }
        }
    }

    public String getFileDir(){return getFileDir() + "/" + fileName;}

    public void setMyAssets(AssetManager inAssets){myAssets = inAssets;}

}
