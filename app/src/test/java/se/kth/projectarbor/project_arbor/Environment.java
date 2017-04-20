package se.kth.projectarbor.project_arbor;

import android.provider.CalendarContract;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fredrik on 2017-04-20.
 */

public class Environment {

    Forecast[] forecasts = new Forecast[3];

    Calendar calendar;

    public enum Weather {
        SUN, RAIN, CLOUDY
    }

    // returning the current weather
    public Weather getWeather() {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD'T'HH:mm:ss.SSSZ");
        String rightNow =dateFormat.format(Calendar.getInstance().getTime());
        Weather weather = getWeatherFromForecast(rightNow);
        return Weather.CLOUDY;
    }

    // return the current temp
    public double getTemp() {
        return 0;
    }


    class Forecast {
        String date;
        int celsius;
        Weather weather;

        public Forecast(String date, int celsius, Weather weather) {
            this.date = date;
            this.celsius = celsius;
            this.weather = weather;
        }

        public boolean dum() {
            return true;
        }


    }

    private Weather getWeatherFromForecast(String rightNow) {
        for (Forecast f : forecasts) {
            rightNow.equals(f.date);
        }
        return Weather.CLOUDY;
    }
}
