package edu.cogswell.morningvoice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Christian on 4/6/2017.
 */

public class Options extends Activity {
    private static final Options ourInstance = new Options();

    private MainActivity.itemReadType [] readOrder;
    private byte readLen;

    //Voice
    //private byte voiceType;
    private int voiceSpeed;

    private int volume;

    //Weather
    private boolean doesWeather;

    private boolean metricSystem;

    private boolean saysCity;
    private boolean saysWind;
    private boolean saysCloudy;
    private boolean saysPressure;
    private boolean saysHumidity;
    private boolean saysSunsetRise;
    private boolean saysHighLowTemp;
    private boolean saysCurTemp;
    private boolean saysPrecipitation;
    private boolean saysVisibility;

    private boolean readsReddit;

    private boolean readsFile;



    public static Options getInstance() {
        return ourInstance;
    }

    private Options() {
        setOptions();
    }



    public static Options getOurInstance() {        return ourInstance; }


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
    public boolean isSaysHighLowTemp() {        return saysHighLowTemp;    }
    public boolean isSaysCurTemp() {        return saysCurTemp;    }
    public boolean isSaysPrecipitation() {        return saysPrecipitation;    }
    public boolean isSaysVisibility(){  return saysVisibility;   }
    public boolean isReadsReddit() {        return readsReddit;    }
    public boolean isReadFile() {        return readsFile;    }
    public MainActivity.itemReadType [] getReadOrder(){ return readOrder;   }
    public byte getReadLen() {  return readLen;  }


    private SeekBar volumeControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeControl = (SeekBar) findViewById(R.id.volume);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                volume = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    public void writeToFile(int groupNum, int childNum, String val){
        try{
            String fileDir = "./././res/values/optionscheck.xml"
            File optionChecks = new File(fileDir);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document boolDoc = dBuilder.parse(optionChecks);

            boolDoc.getDocumentElement().normalize();

            Element root = boolDoc.getDocumentElement();

            NodeList weatherOp = root.getElementsByTagName("Weather");

            String [] headerList = {"Options", "Weather", "Reddit", "File"};
            String [][] childList = {
                    {},
                    {"reads", "city", "temperature", "humidity",
                            "pressure", "wind", "clouds", "visibility", "precipitation", "sun"},
                    {"reads"},
                    {"reads"}
            };

            ((Element)(root.getElementsByTagName(headerList[groupNum])).item(0))
                    .getAttributes().getNamedItem(childList[groupNum][childNum]).setNodeValue(val);

            //  !!! ASK FOR HELP

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(optionChecks, new StreamResult(new File(fileDir)));

        }catch(Exception ex){
            System.out.printf("Exiting on writeToFile()");
        }


    }


    public void setOptions(){

        setContentView(R.layout.activity_main);

        //voice type/volume
        volume = ((SeekBar)findViewById(R.id.volume)).getProgress();

        try{
            // XML File
            File optionChecks = new File("./././res/values/optionscheck.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document boolDoc = dBuilder.parse(optionChecks);

            boolDoc.getDocumentElement().normalize();

            Element root = boolDoc.getDocumentElement();

            NodeList weatherOp = root.getElementsByTagName("Weather");
            String [] weatherList = {"reads", "city", "temperature", "humidity",
                "pressure", "wind", "clouds", "visibility", "precipitation", "sun"};
            boolean [] weathBool = new boolean [weatherOp.getLength()];

            for (int i = 0; i < weatherOp.getLength(); i++){
                Boolean.getBoolean(((Element)weatherOp.item(i)).getAttribute(weatherList[i]));
            }
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

        }catch(FileNotFoundException ex){

        }catch(Exception e){

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


}
