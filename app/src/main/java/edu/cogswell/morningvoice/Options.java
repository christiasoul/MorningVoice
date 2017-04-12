package edu.cogswell.morningvoice;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.SeekBar;

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

    private boolean saysWind;
    private boolean saysCloudy;
    private boolean saysPressure;
    private boolean saysHumidity;
    private boolean saysSunsetRise;
    private boolean saysHighLowTemp;
    private boolean saysCurTemp;
    private boolean saysPrecipitation;
    private boolean saysUVIndex;
    private boolean saysAirPollution;

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
    public boolean isDoesWeather() {        return doesWeather;    }
    public boolean isSaysWind() {        return saysWind;    }
    public boolean isSaysCloudy() {        return saysCloudy;    }
    public boolean isSaysPressure() {        return saysPressure;    }
    public boolean isSaysHumidity() {        return saysHumidity;    }
    public boolean isSaysSunsetRise() {        return saysSunsetRise;    }
    public boolean isSaysHighLowTemp() {        return saysHighLowTemp;    }
    public boolean isSaysCurTemp() {        return saysCurTemp;    }
    public boolean isSaysPercipitation() {        return saysPrecipitation;    }
    public boolean isSaysUVIndex() {        return saysUVIndex;    }
    public boolean isSaysAirPolution() {        return saysAirPollution;    }
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


    public void setOptions(){

        setContentView(R.layout.activity_main);

        //voice type/volume
        volume = ((SeekBar)findViewById(R.id.volume)).getProgress();


        // Set order

        doesWeather = ((CheckBox)findViewById(R.id.doesWeather)).isChecked();
        saysWind = ((CheckBox)findViewById(R.id.saysWind)).isChecked();
        saysCloudy = ((CheckBox)findViewById(R.id.saysCloudy)).isChecked();
        saysPressure = ((CheckBox)findViewById(R.id.saysPressure)).isChecked();
        saysHumidity = ((CheckBox)findViewById(R.id.saysHumidity)).isChecked();
        saysSunsetRise = ((CheckBox)findViewById(R.id.saysSunsetRise)).isChecked();
        saysHighLowTemp = ((CheckBox)findViewById(R.id.saysHighLowTemp)).isChecked();
        saysCurTemp = ((CheckBox)findViewById(R.id.saysCurTemp)).isChecked();
        saysPrecipitation = ((CheckBox)findViewById(R.id.saysPrecipitation)).isChecked();
        saysUVIndex = ((CheckBox)findViewById(R.id.saysUVIndex)).isChecked();
        saysAirPollution = ((CheckBox)findViewById(R.id.saysAirPollution)).isChecked();

        readsReddit = ((CheckBox)findViewById(R.id.readsReddit)).isChecked();

        readsFile = ((CheckBox)findViewById(R.id.readsFile)).isChecked();

        readLen = 0;
        if (readsReddit) readLen++;
        if (doesWeather) readLen++;
        if (readsFile) readLen++;



    }


}
