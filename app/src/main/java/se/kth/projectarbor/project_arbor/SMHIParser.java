package se.kth.projectarbor.project_arbor;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
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

class SMHIParser /*implements Serializable */{

    //private static final long serialVersionUID = 7746966029121214890L;

    private double LATITUDE;
    private double LONGITUDE;
    private String CATEGORY = "pmp2g";
    private int VERSION = 2;
    private String START_URL = "http://opendata-download-metfcst.smhi.se";
    private Environment.Forecast[] forcasts;
    private Calendar rightNow;

    private SMHIParser() {
    }

    public SMHIParser(double LATITUDE, double LONGITUDE) {
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
    }

    public Environment.Forecast[] getForecast(Calendar rightNow) {
        this.rightNow = rightNow;
        String url = START_URL;

        String longitude = String.format(Locale.ENGLISH, "%.6f", LONGITUDE);
        String latitude = String.format(Locale.ENGLISH, "%.6f", LATITUDE);

        url = url.concat("/api/category/" + CATEGORY + "/version/" + VERSION +
                "/geotype/point/lon/" + longitude + "/lat/"+ latitude + "/data.json");

        // TODO: waiting for other thread to be done, fixme later
        // new GetUrl().execute(url);

        try {
            JSONtostring(url);
        } catch (Exception e) {
            Log.e("ARBOR", e.toString());
        }

        return forcasts;
    }

    /*
    private class GetUrl extends AsyncTask<String, Void, String> {
       // private static final long serialVersionUID = 4326855909443156638L;

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONtostring(params[0]);
            } catch (Exception e) {
                Log.e("ARBOR", e.toString());
            }
            return "";
        }

    }*/


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

        this.forcasts = forecasts;
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
        if(code == 1 || code == 2 || code == 3 || code == 4){
            return Environment.Weather.SUN;
        }
        else if(code == 5 || code == 6 || code == 7){
            return Environment.Weather.CLOUDY;
        }
        else {
            return Environment.Weather.RAIN;
        }
    }

}