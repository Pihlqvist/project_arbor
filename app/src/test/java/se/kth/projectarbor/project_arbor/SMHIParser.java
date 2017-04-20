package se.kth.projectarbor.project_arbor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

/**
 * Created by Fredrik Pihlqvist on 2017-04-20.
 *
 *
 * This class is for parsing SMHIS API and returning relevant information that this
 * class decides
 */

public class SMHIParser {

    private double LONGITUDE = 18.068581;
    private double LATITUDE = 59.329323;
    private String CATEGORY = "pmp2g";
    private int VERSION = 2;
    private String START_URL = "http://opendata-download-metfcst.smhi.se";


    // Returns a list of Forecast objects, this method will make a connection with SMHI
    // and parse the results and store them in a array
    public Environment.Forecast[] getForecast() throws Exception {
        String url = START_URL;
        url = url.concat("/api/category/" + CATEGORY + "/version/" + VERSION +
                "/geotype/point/lon/" + LONGITUDE + "/lat/"+ LATITUDE + "/data.json");
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

        return parseJSON(result);
    }

    // This method parses a string with JSON format from the SMHI webpage and returns an array
    // of Forecast objects
    private Environment.Forecast[] parseJSON(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        JSONArray timeSeries = jsonObject.getJSONArray("timeSeries");
        Environment.Forecast[] forecasts = new Environment.Forecast[3];
        JSONObject serie, temprature, wsymb;
        JSONArray parameters;
        Calendar date;
        double temp;
        Environment.Weather weather;

        for (int i=0; i<3; i++) {
            serie = timeSeries.getJSONObject(i);
            date = jsonTimeParser(serie.getString("validTime"));

            parameters = serie.getJSONArray("parameters");
            temprature = parameters.getJSONObject(1);
            wsymb = parameters.getJSONObject(18);

            temp = temprature.getJSONArray("values").getDouble(0);
            weather = decodeWeather(wsymb.getJSONArray("values").getInt(0));

            forecasts[i] = new Environment.Forecast(date, temp, weather);
        }

        return forecasts;
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
        calendar.set(year, month, day, hour, 0, 0);

        return calendar;
    }

    // Decode an int between 1-15 to a specific ENUM
    private Environment.Weather decodeWeather(int code) {
        // TODO get this to return the right thing
        return Environment.Weather.CLOUDY;
    }


    @Test // for testing only
    public void testParser() throws Exception {
        Environment.Forecast[] forecasts = getForecast();
        for (Environment.Forecast f : forecasts) {
            System.out.println(f.toString());
        }
    }
}
