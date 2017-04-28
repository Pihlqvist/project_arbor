package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

public class Environment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // private static final long serialVersionUID = 2265456326653633040L;
    private final long locationUpdateInterval = 1000*60*5;
    private final float DISTANCE_LIMIT = 2000;

    private Forecast[] forecasts = {};
    private SMHIParser parser;
    private Location oldLocation;
    private Location newLocation;
    private Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            oldLocation = newLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } catch (SecurityException ex) {
            // TODO
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO
        try {
            Thread.sleep(1000);
            googleApiClient.connect();
        } catch (InterruptedException ex) { }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("ARBOR_ENV", "onLocationChanged()");
        newLocation = location;
    }


    // Interpet SMHI symbol data
    public enum Weather {
        SUN, RAIN, CLOUDY
    }

    public Environment(Context context) {
        // this.forecasts = getForecasts();
        this(context, new Forecast[]{});
    }

    public Environment(Context context, Forecast[] forecasts) {
        // TODO
        this.parser = new SMHIParser();
        this.forecasts = forecasts;

        this.context = context;
        //TODO: this.mEnvironment = environment;

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(locationUpdateInterval);
    }

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
        Calendar rightNow = Calendar.getInstance();
        return getWeatherFromForecast(rightNow);
    }

    // return the current temp
    public double getTemp() {
        Calendar rightNow = Calendar.getInstance();
        return getTempFromForecast(rightNow);
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
            Log.e("ARBOR", "error: " + e);
        }

        Log.d("ARBOR", "getForecast() failed");
        return Weather.CLOUDY;
    }

    private double newTempForecast(Calendar rightNow) {

        try {
            forecasts = parser.getForecast(rightNow);
            return forecasts[0].celsius;
        } catch (Exception e) {
            Log.e("ARBOR", "catch " + e);
        }

        return Double.NaN;
    }
    // Looks trough the cached data and determins if its oke or new is needed
    private Weather getWeatherFromForecast(Calendar rightNow) {
        Weather weather;

        if (oldLocation.distanceTo(newLocation) > DISTANCE_LIMIT) {
            parser = new SMHIParser(newLocation.getLatitude(), newLocation.getLongitude());
            oldLocation = newLocation;
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

        if (oldLocation.distanceTo(newLocation) > DISTANCE_LIMIT) {
            parser = new SMHIParser(newLocation.getLatitude(), newLocation.getLongitude());
            oldLocation = newLocation;
            return newTempForecast(rightNow);
        }

        if (forecasts == null || forecasts.length < 1) { newTempForecast(rightNow); }

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