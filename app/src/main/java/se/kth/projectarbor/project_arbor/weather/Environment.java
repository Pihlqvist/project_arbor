package se.kth.projectarbor.project_arbor.weather;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.*;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Fredrik Pihlqvist, Johan <Placeholder> and Joseph Ariss on 2017-04-20.
 *
 * This class is taking care of everything around the tree, you are able to make calls to
 * this class without having to think about system resources. Information is being cached
 * by this class so that calls to public webpages are not frequent
 */

public class Environment implements android.location.LocationListener {

    private final static String TAG = "ARBOR_ENVIRONMENT";

    private final static long LOCATION_UPDATE_INTERVAL = 10 * 1000;  // How much it updates in sec
    // distance in meter you have to travel to get a new coordinate
    private final static float DISPLACEMENT_LIMIT = 2 * 1000;

    private Forecast[] forecasts = {};
    private SMHIParser parser;
    private Location newLocation;
    private Context context;
    private boolean displacementExceeded = false;

    // Interpet SMHI symbol data
    public enum Weather {
        SUN, RAIN, CLOUDY, PARTLY_CLOUDY, NOT_AVAILABLE
    }

    public Environment(Context context) {
        this(context, new Forecast[]{});
    }

    public Environment(Context context, Forecast[] forecasts) {
        android.location.LocationManager locationManager =
                (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled =
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        if(isNetworkEnabled) {
            int selfPermission = ContextCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_COARSE_LOCATION);

            if (selfPermission == PackageManager.PERMISSION_GRANTED) {
                newLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL,
                        DISPLACEMENT_LIMIT, this);

                // Setup parser with coordinates
                if (newLocation != null) {
                    this.parser = new SMHIParser(newLocation.getLatitude(), newLocation.getLongitude());
                } else {
                    newLocation = new Location("GPS");
                    this.parser = new SMHIParser(59.858563,17.638926);
                    Log.e(TAG, "Location received was null");
                }
            } else {
                Log.e(TAG, "PackageManager did not give permission");
            }
        } else {
            Log.e(TAG, "Network was not available");
        }


        this.forecasts = forecasts;
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
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
        if (forecasts == null) {
            return new Forecast[] {};
        } else {
            return Arrays.<Forecast>copyOf(forecasts, forecasts.length);
        }
    }

    // Ask the SMHIParser for new data and store it in the class.
    private Weather newForecast(Calendar rightNow) {

        if (isNetworkAvailable()) {
            try {
                forecasts = parser.getForecast(rightNow);
                return forecasts[0].weather;
            } catch (Exception e) {
                Log.e(TAG, "catch: " + e);
            }
        }

        return Weather.NOT_AVAILABLE;
    }

    private double newTempForecast(Calendar rightNow) {

        if (isNetworkAvailable()) {
            try {
                forecasts = parser.getForecast(rightNow);
                return forecasts[0].celsius;
            } catch (Exception e) {
                Log.e(TAG, "catch: " + e);
            }
        }

        return Double.NaN;
    }
    // Looks trough the cached data and determine if its relevant or new data is needed
    private Weather getWeatherFromForecast(Calendar rightNow) {
        Weather weather;

        if (newLocation == null) {
            return Weather.NOT_AVAILABLE;
        }

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

        if (newLocation == null) {
            return Double.NaN;
        }

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

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return  true;
        } else {
            return false;
        }

    }

}