package se.kth.projectarbor.project_arbor;

import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fredrik, johan and joseph on 2017-04-20.
 *
 * This class is taking care of everything around the tree, you are able to make calls to
 * this class without having to think about system resources. Information is being cached
 * by this class so that calls to public webpages are not frequent
 */

public class Environment implements Serializable {

    private static final long serialVersionUID = 2265456326653633040L;
    private Forecast[] forecasts;
    private SMHIParser parser;


    // Interpet SMHI symbol data
    public enum Weather {
        SUN, RAIN, CLOUDY
    }

    public Environment(double LATITUDE, double LONGITUDE) {
        this.parser = new SMHIParser(LATITUDE, LONGITUDE);
    }

    public static class Forecast implements Serializable {

        private static final long serialVersionUID = 5714561621911257132L;

        Calendar date;
        double celsius;
        Weather weather;

        public Forecast(Calendar date, double celsius, Weather weather) {
            this.date = date;
            this.celsius = celsius;
            this.weather = weather;
        }

        public String toString() {
            return "DATE: " + date.getTime() + "\nTEMP: " + celsius + "\nWEATHER: "
                    + weather.toString() + "\n";
        }

    }
    // returning the current weather
    public Weather getWeather() {
        Calendar rightNow = Calendar.getInstance();
        return getWeatherFromForecast(rightNow);
    }

    // return the current temp
    public double getTemp() {
        Calendar rightNow = Calendar.getInstance();
        return getTempFromForecast(rightNow);
    }

    // Ask the SMHIParser for new data and store it in the class.
    private Weather newForecast(Calendar rightNow) {

        try {
            forecasts = parser.getForecast(rightNow);
        } catch (Exception e) {
            Log.e("ERROR", "error: " + e);
        }

        return forecasts[0].weather;
    }

    private double newTempForecast(Calendar rightNow) {

        try {
            forecasts = parser.getForecast(rightNow);
        } catch (Exception e) {
            Log.e("ARBOR", "catch " + e);
        }

        return forecasts[0].celsius;
    }
    // Looks trough the cached data and determins if its oke or new is needed
    private Weather getWeatherFromForecast(Calendar rightNow) {
        Weather weather;
        if (forecasts == null) { newForecast(rightNow); }

        if (rightNow.before(forecasts[0].date)) {
            weather = forecasts[0].weather;
        } else if (rightNow.before(forecasts[1].date)) {
            weather = forecasts[1].weather;
        } else if (rightNow.before(forecasts[2].date)) {
            weather = forecasts[2].weather;
        } else {
            weather = newForecast(rightNow);
        }

        return weather;
    }

    public double getTempFromForecast(Calendar rightNow){
        double temperature;
        if (forecasts == null) { newTempForecast(rightNow); }

        if (rightNow.before(forecasts[0].date)) {
            temperature = forecasts[0].celsius;
        } else if (rightNow.before(forecasts[1].date)) {
            temperature = forecasts[1].celsius;
        } else if (rightNow.before(forecasts[2].date)) {
            temperature = forecasts[2].celsius;
        } else {
            temperature = newTempForecast(rightNow);
        }

        return temperature;
    }
}