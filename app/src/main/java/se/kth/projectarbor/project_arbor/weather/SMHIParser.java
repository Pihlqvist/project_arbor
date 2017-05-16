package se.kth.projectarbor.project_arbor.weather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by fredrik, johan and joseph on 2017-04-20.
 *
 *
 * This class is for parsing SMHIS API and returning relevant information that this
 * class decides
 */

class SMHIParser {

    private final static String TAG = "ARBOR_SMHIPARSER";

    private double LATITUDE;
    private double LONGITUDE;
    private final static String FAIL_SAFE_LONGITUDE = "17.638926";
    private final static String FAIL_SAFE_LATITUDE  = "59.858563";
    private final static String CATEGORY = "pmp2g";
    private final static int VERSION = 2;
    private final static String START_URL = "http://opendata-download-metfcst.smhi.se";

    private Environment.Forecast[] forecasts;
    private Calendar rightNow;



    public SMHIParser(double LATITUDE, double LONGITUDE) {
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
    }

    public Environment.Forecast[] getForecast(Calendar rightNow) {
        this.rightNow = rightNow;

        // Locale.ENGLISH is used because format() may use a e.g. comma when converting a float to
        // a string; SMHI only understands periods
        String longitude = String.format(Locale.ENGLISH, "%.6f", LONGITUDE);
        String latitude = String.format(Locale.ENGLISH, "%.6f", LATITUDE);

        String url = START_URL + "/api/category/" + CATEGORY + "/version/" + VERSION +
                "/geotype/point/lon/" + longitude + "/lat/"+ latitude + "/data.json";

        try {
            JSONtostring(url);
        } catch (Exception e) {
            Log.e(TAG, e.toString());

            // Fail safe location is used instead
            url = START_URL + "/api/category/" + CATEGORY + "/version/" + VERSION +
                    "/geotype/point/lon/" + FAIL_SAFE_LONGITUDE +
                    "/lat/"+ FAIL_SAFE_LATITUDE + "/data.json";

            try {
                JSONtostring(url);
            } catch (Exception exceptionAgain) {
                Log.e(TAG, exceptionAgain.toString());
                this.forecasts = new Environment.Forecast[] {};
            }
        }
        return this.forecasts;
    }


    // Returns a list of Forecast objects, this method will make a connection with SMHI
    // and parse the results and store them in a array
    private void JSONtostring(String url) throws Exception {
        InputStream inputStream = new URL(url).openStream();
        String result;


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            StringBuilder stringBuilder = new StringBuilder();
            int cp;
            while ((cp = reader.read()) != -1) {
                stringBuilder.append((char) cp);
            }
            result = stringBuilder.toString();
        } finally {
            inputStream.close();
        }


        parseJSON(result);
    }

    // This method parses a string with JSON format from the SMHI webpage and returns an array
    // of Forecast objects
    private void parseJSON(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        JSONArray timeSeries = jsonObject.getJSONArray("timeSeries");
        Environment.Forecast[] forecasts = new Environment.Forecast[3];
        JSONObject serie, temprature, wsymb;
        JSONArray parameters;
        Calendar date;
        double temp;
        Environment.Weather weather;
        int plats = 0;
        for (int i=0; i<timeSeries.length();i++){
            serie = timeSeries.getJSONObject(i);
            date = jsonTimeParser(serie.getString("validTime"));
            if (date.after(rightNow)){
                plats = i;

                break;
            }
        }

        for (int i=plats; i<plats+3; i++) {
            serie = timeSeries.getJSONObject(i);
            date = jsonTimeParser(serie.getString("validTime"));

            parameters = serie.getJSONArray("parameters");
            temprature = parameters.getJSONObject(1);
            wsymb = parameters.getJSONObject(18);

            temp = temprature.getJSONArray("values").getDouble(0);
            weather = decodeWeather(wsymb.getJSONArray("values").getInt(0));

            forecasts[i-plats] = new Environment.Forecast(date, temp, weather);
        }

        this.forecasts = forecasts;
    }

    // Takes a string with time information from SMHI API JSON file and returns a
    // Calender object with the information excluding minutes and seconds
    private Calendar jsonTimeParser(String jsonTime) {
        Calendar calendar = Calendar.getInstance();

        String[] strings = jsonTime.split("-|:|T|Z");

        int year = Integer.parseInt(strings[0]);
        int month = Integer.parseInt(strings[1]);
        int day = Integer.parseInt(strings[2]);
        int hour = Integer.parseInt(strings[3]);
        calendar.set(year, month-1, day, hour, 0, 0);

        return calendar;
    }

    // Decode an int between 1-15 to a specific ENUM
    private Environment.Weather decodeWeather(int code) {
        if(code == 1 || code == 2){
            return Environment.Weather.SUN;
        } else if (code == 3 || code == 4) {
            return Environment.Weather.PARTLY_CLOUDY;
        } else if(code == 5 || code == 6 || code == 7){
            return Environment.Weather.CLOUDY;
        } else {
            return Environment.Weather.RAIN;
        }
    }

}