package se.kth.projectarbor.project_arbor;

import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fredrik, johan and josef on 2017-04-20.
 *
 * This class is taking care of everything around the tree, you are able to make calls to
 * this class without having to think about system resources. Information is being cached
 * by this class so that calls to public webpages are not frequent
 */

public class Environment {

    private Forecast[] forecasts;

    // Interpet SMHI symbol data
    public enum Weather {
        SUN, RAIN, CLOUDY
    }

    public static class Forecast {
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
        rightNow.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, 0, 0);
        return getWeatherFromForecast(rightNow);
    }

    // return the current temp
    public double getTemp() {
        return 0;
    }

    // Ask the SMHIParser for new data and store it in the class.
    private Weather newForecast() {
        SMHIParser parser = new SMHIParser();

        try {
            forecasts = parser.getForecast();
        } catch (Exception e) {
            Log.e("ERROR", "error: " + e);
        }

        return forecasts[0].weather;
    }

    // Looks trough the cached data and determins if its oke or new is needed
    private Weather getWeatherFromForecast(Calendar rightNow) {
        Weather weather;
        if (forecasts == null) { newForecast(); }

        if (rightNow.equals(forecasts[0].date)) {
            weather = forecasts[0].weather;
        } else if (rightNow.equals(forecasts[1].date)) {
            weather = forecasts[1].weather;
        } else if (rightNow.equals(forecasts[2].date)) {
            weather = forecasts[2].weather;
        } else {
            weather = newForecast();
        }

        return weather;
    }
}
