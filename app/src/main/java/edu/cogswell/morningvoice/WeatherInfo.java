package edu.cogswell.morningvoice;

import android.location.Location;
import android.os.SystemClock;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Christian on 4/12/2017.
 */

public class WeatherInfo {
    private final String weatherLoc = "./././res/xml/weatherInfo.xml";

    private String cityName;
    private String curRainType; // volume for last 3 hours
    private float curRainAmt;

    private String Overall;

    private float curTemp; // Kelvin
    private float curPressure; // hPa
    private float curHumidity; // %

    private float curVisibility; // Meters

    private float curWindSpeed; // meters/sec
    private float curWindDirection; // degrees (meteorological)
    private String curWindDirName;

    private float curCloudy; //%
    private String curCloudyName;


    private String curSunsetTime;
    private String curSunriseTime;

    private String curWeather;


    private enum ConversionUnit{Distance, Temperature}

    private boolean usesMetric;



    private void readFromFile(){

        try {
            //
            File weatherXml = new File("Some Location");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document weatherDoc = dBuilder.parse(weatherXml);

            weatherDoc.getDocumentElement().normalize();

            Element root = weatherDoc.getDocumentElement();

            Element city, sun, temperature, humidity, pressure, wind, speed,
                    direction, clouds, visibility, precipitation, weather;

            city = getElement(root, "city");
            sun = getElement(city, "sun");
            temperature = getElement(root, "temperature");
            humidity = getElement(root, "humidity");
            pressure = getElement(root, "pressure");
            wind = getElement(root, "wind");
            speed = getElement(wind, "speed");
            direction = getElement(wind, "direction");
            clouds = getElement(root, "clouds");
            visibility = getElement(root, "visibility");
            precipitation = getElement(root, "precipitation");
            weather = getElement(root, "weather");

            if (city != null){
                cityName = city.getAttribute("name");
            }else{
                cityName = null;
            }

            if (sun != null){
                curSunriseTime = (sun.getAttribute("rise").split("T"))[1];
                curSunsetTime = (sun.getAttribute("set").split("T"))[1];
            }else {
                curSunriseTime = null;
                curSunsetTime = null;
            }

            if (temperature != null) {
                curTemp = Float.parseFloat(temperature.getAttribute("value"));
            }else{
                curTemp = Float.NaN;
            }

            if (humidity != null){
                curHumidity = Float.parseFloat(humidity.getAttribute("value"));
            }else {
                curHumidity = Float.NaN;
            }

            if (pressure != null){
                curPressure = Float.parseFloat(pressure.getAttribute("value"));
            }else {
                curPressure = Float.NaN;
            }

            if (speed != null){
                curWindSpeed = Float.parseFloat(speed.getAttribute("value"));
            }else{
                curWindSpeed = Float.NaN;
            }

            if (direction != null){
                curWindDirection = Float.parseFloat(direction.getAttribute("value"));
                curWindDirName = direction.getAttribute("name");
            }else {
                curWindDirection = Float.NaN;
                curWindDirName = null;
            }

            if (clouds != null){
                curCloudy = Float.parseFloat(clouds.getAttribute("value"));
                curCloudyName = clouds.getAttribute("name");
            }else{
                curCloudy = Float.NaN;
                curCloudyName = null;
            }

            if (visibility != null){
                curVisibility = Float.parseFloat(visibility.getAttribute("value"));
            }else{
                curVisibility = Float.NaN;
            }

            if (precipitation != null){
                if (precipitation.getAttribute("mode") .equals( "snow")){
                    curRainType = "snow";
                    curRainAmt = Float.parseFloat(precipitation.getAttribute("value"));
                }else if (precipitation.getAttribute("mode").equals( "rain")){
                    curRainType = "rain";
                    curRainAmt = Float.parseFloat(precipitation.getAttribute("value"));
                }else{
                    curRainType = "No";
                }
            }else{
                curRainType = "No";
            }

            if (weather != null){
                curWeather = weather.getAttribute("value");
            }else {
                curWeather = null;
            }


        }catch(Exception e){
            System.out.printf("There seemed to not be a file to read from for weather");

        }

    }

    private Element getElement(Element root, String name)throws Exception{
        try {
            if (root != null) {
                NodeList nodeEle = root.getElementsByTagName(name);
                if (nodeEle.getLength() > 0) {
                    return (Element) nodeEle.item(0);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }catch (Exception e){
            throw e;
        }
    }

    public WeatherInfo(){

    }

    private void getWebDataToFile(String call){
        HttpURLConnection connec = null;
        InputStream inStream = null;
        OutputStream outStream = null;

        try{
            connec = (HttpURLConnection)(new URL(call)).openConnection();
            connec.setRequestMethod("GET");
            connec.setDoInput(true);
            connec.setDoOutput(true);
            connec.connect();

            inStream = connec.getInputStream();
            outStream = new FileOutputStream(weatherLoc);
            byte[] buff = new byte[8 * 1024];
            int bytesRead;
            while((bytesRead = inStream.read(buff)) != -1){
                outStream.write(buff, 0, bytesRead);
            }
            IOUtils.closeQuietly(inStream);
            IOUtils.closeQuietly(outStream);


        }catch(Throwable t){
            System.out.printf("Could not get information from website and write to file");
        }

    }

    public long update(Location loc){

        //GPS Things
        float latit = (float)loc.getLatitude();
        float longit = (float)loc.getLongitude();

        String weatherCall = String.format(
                "api.openweathermap.org/data/2.5/weather?lat=%f.2&lon=%f.2&%s",
                latit, longit, APIKey.getInstance().getAPIKey());

        getWebDataToFile(weatherCall);
        readFromFile();
        return SystemClock.elapsedRealtime();
    }

    public long update(){

        int zipCode = Options.getInstance().getZipCode();
        String countryCode = Options.getInstance().getCountryCode();

        String weatherCall = String.format(
            "api.openweathermap.org/data/2.5/weather?zip=%d,%s&%s",
                zipCode, countryCode, APIKey.getInstance().getAPIKey() );

        getWebDataToFile(weatherCall);
        readFromFile();
        return SystemClock.elapsedRealtime();
    }

    public String getString(){
        Options curOptions = Options.getInstance();

        if (curOptions.isDoesWeather()){
            String retString = "Today";

            usesMetric = curOptions.isMetricSystem();

            if (curWeather != null){
                retString += " the weather is " + curWeather + ".";
            }
            if (cityName != null && curOptions.isSaysCity()){
                retString += " in " + cityName + ".";
            }
            if (!Float.isNaN(curTemp) && curOptions.isSaysCurTemp()){
                retString += " it is " + String.format("%.2f",convertUnits(ConversionUnit.Temperature , curTemp, false));
                retString += " degrees " + getUnit(ConversionUnit.Temperature, false) + ".";
            }
            if (!Float.isNaN(curHumidity) && curOptions.isSaysHumidity()){
                retString += " the humidity is " + String.format("%.1f", curHumidity) + " percent " + ".";
            }
            if (!Float.isNaN(curPressure) && curOptions.isSaysPressure()){
                retString += " the pressure is " + String.format("%.2f", curPressure) + " hecto Pascals " + ".";
            }
            if (!Float.isNaN(curWindSpeed) && curOptions.isSaysWind()){
                retString += " the wind speed is " + String.format(("%.2f "), convertUnits(ConversionUnit.Distance ,curWindSpeed, false));
                retString += getUnit(ConversionUnit.Distance, false) + " per second.";
            }
            if (curWindDirName != null && curOptions.isSaysWind()){
                retString += " the wind is moving in the " + curWindDirName + " direction";
            }
            if (curCloudyName != null && curOptions.isSaysCloudy()) {
                retString += " currently there is " + curCloudyName;
                if (!Float.isNaN(curCloudy)) {
                    retString += String.format("and it is %.2f %% cloudy", curCloudy);
                }
                retString += ".";
            }
            if (!Float.isNaN(curVisibility) && curOptions.isSaysVisibility()){
                retString += String.format("the visibility is limited to %f %s.",
                        convertUnits(ConversionUnit.Distance, curVisibility, false), getUnit(ConversionUnit.Distance, false));
            }
            if (curRainType != null && curOptions.isSaysPrecipitation()){

                if(curRainType.contentEquals("no")){
                    retString += " there is no rain currently";
                }else {
                    retString += " it is currently " + curRainType + "ing and has " + curRainType + "ed ";
                    retString += String.format("%.2f %s", convertUnits(ConversionUnit.Distance, curRainAmt, true),
                            getUnit(ConversionUnit.Distance, true));
                }
            }
            if (curSunriseTime != null && curOptions.isSaysSunsetRise()){
                retString += String.format(" the sun rises at %s .", curSunriseTime);
            }
            if (curSunsetTime != null && curOptions.isSaysSunsetRise()){
                retString += String.format(" the sun sets at %s . ", curSunsetTime);
            }

            return retString;

        }else {
            return null;
        }

    }



    private float convertUnits(ConversionUnit unit, float item, boolean rain){
        if (usesMetric){
            if (unit == ConversionUnit.Temperature){
                return item - 273.14f;
            }else {
                return item;
            }
        }else {
            if (unit == ConversionUnit.Distance) {
                if (rain){
                    return (item * .0393701f);
                }else{
                    return (item * 3.28084f);
                }
            } else if (unit == ConversionUnit.Temperature) {
                return ((item * 1.8f) - 459.67f);
            }else{
                return 0;
            }
        }
    }

    private String getUnit(ConversionUnit unit, boolean rain){
        if (unit == ConversionUnit.Distance){
            if (usesMetric){
                if (rain){
                    return "millimeters";
                }else {
                    return "meters";
                }
            }else {
                if(rain){
                    return "inches";
                }else {
                    return "feet";
                }
            }
        }else if (unit == ConversionUnit.Temperature){
            if (usesMetric){
                return "Celcius";
            }else{
                return "Farenheit";
            }
        }else {
            return null;
        }
    }



}
