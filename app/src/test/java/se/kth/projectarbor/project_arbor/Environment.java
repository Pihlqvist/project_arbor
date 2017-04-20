package se.kth.projectarbor.project_arbor;

import android.os.Build;
import android.provider.CalendarContract;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fredrik, johan and josef on 2017-04-20.
 */

public class Environment {

    Forecast[] forecasts;


    public enum Weather {
        SUN, RAIN, CLOUDY
    }

    // returning the current weather
    public Weather getWeather() {
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, 0, 0);
        Weather weather = getWeatherFromForecast(rightNow);


        return Weather.CLOUDY; // weather;
    }

    // return the current temp
    public double getTemp() {
        return 0;
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

    private Weather newForecast() {
        // TODO use method from SMHIParser
        return Weather.CLOUDY;
    }

    private Weather getWeatherFromForecast(Calendar rightNow) {
        Weather weather;
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
