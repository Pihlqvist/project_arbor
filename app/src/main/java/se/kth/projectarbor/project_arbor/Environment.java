package se.kth.projectarbor.project_arbor;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by fredrik, johan and joseph on 2017-04-20.
 *
 * This class is taking care of everything around the tree, you are able to make calls to
 * this class without having to think about system resources. Information is being cached
 * by this class so that calls to public webpages are not frequent
 */

public class Environment implements android.location.LocationListener {

    // private static final long serialVersionUID = 2265456326653633040L;
    private final long LOCATION_UPDATE_INTERVAL = 2 * 1000; // 1000*60*5;
    private final long LOCATION_UPDATE_INTERVAL_FAST = 5000; // 1000*60*4;
    private final float DISPLACEMENT_LIMIT = 2000; // 2000;

    private Forecast[] forecasts = {};
    private SMHIParser parser;
    private Location newLocation;
    private Context context;
    private boolean displacementExceeded = false;

    private android.location.LocationManager locationManager;
    private boolean isNetworkEnabled;

    // Interpet SMHI symbol data
    public enum Weather {
        SUN, RAIN, CLOUDY, NAN
    }

    public Environment(Context context) {
        this(context, new Forecast[]{});
    }

    public Environment(Context context, Forecast[] forecasts) {
        locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        if(isNetworkEnabled) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                newLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("ARBOR_ENV", "Start Location: " + newLocation.getLatitude() + ", " + newLocation.getLongitude());
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, DISPLACEMENT_LIMIT, this);
                Log.d("ARBOR_ENV", "requestLocationUpdates() done");
            } else {
                // TODO: request permission from user
                // UPPSALA
                newLocation = new Location("");
                newLocation.setLatitude(59.858563);
                newLocation.setLongitude(17.638926);
                Log.d("ARBOR_ENV", "dint get permission");
            }
        }

        Log.d("ARBOR_ENV", "before parser");
        this.parser = new SMHIParser(newLocation.getLatitude(), newLocation.getLongitude());
        Log.d("ARBOR_ENV", "after parser, before forecast");

        // TODO: ask for permission from the user to use internet
        if (forecasts.length < 1) {
            this.forecasts = parser.getForecast(Calendar.getInstance());
        } else {
            this.forecasts = forecasts;
        }
        Log.d("ARBOR_ENV", "after forecast");

        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ARBOR_ENV", "onLocationChanged()");
        Log.d("ARBOR_ENV", "Coordinates: " + location.getLatitude() + ", " + location.getLongitude());
        displacementExceeded = true;
        newLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }


    public static class Forecast implements Serializable {

        private static final long serialVersionUID = 5714561621911257132L;
        private Calendar date;
        private double celsius;
        private Weather weather;

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
        return getWeatherFromForecast(Calendar.getInstance());
    }

    // return the current temp
    public double getTemp() {
        return getTempFromForecast(Calendar.getInstance());
    }

    public Forecast[] getForecasts() {
        return Arrays.<Forecast>copyOf(forecasts, forecasts.length);
    }

    // Ask the SMHIParser for new data and store it in the class.
    private Weather newForecast(Calendar rightNow) {

        try {
            forecasts = parser.getForecast(rightNow);
            return forecasts[0].weather;
        } catch (Exception e) {
            Log.e("ARBOR_ENV", "catch: " + e);
        }

        return Weather.NAN;
    }

    private double newTempForecast(Calendar rightNow) {

        try {
            forecasts = parser.getForecast(rightNow);
            return forecasts[0].celsius;
        } catch (Exception e) {
            Log.e("ARBOR_ENV", "catch: " + e);
        }

        return Double.NaN;
    }
    // Looks trough the cached data and determine if its relevant or new data is needed
    private Weather getWeatherFromForecast(Calendar rightNow) {
        Weather weather;

        if (displacementExceeded) {
            parser = new SMHIParser(newLocation.getLatitude(), newLocation.getLongitude());
            displacementExceeded = false;
            return newForecast(rightNow);
        }

        if (forecasts == null || forecasts.length < 1) {
            return newForecast(rightNow);
        }

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

        if (displacementExceeded) {
            parser = new SMHIParser(newLocation.getLatitude(), newLocation.getLongitude());
            displacementExceeded = false;
            return newTempForecast(rightNow);
        }

        if (forecasts == null || forecasts.length < 1) {
            return newTempForecast(rightNow);
        }

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