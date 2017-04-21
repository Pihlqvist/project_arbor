package se.kth.projectarbor.project_arbor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Ramcin on 2017-04-21.
 */

class LocationManager implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final static int MSG_START_RUN = 1;
    final static int MSG_STOP_RUN = 2;

    //private long LOCATION_UPDATE_INTERVAL = 10000;
    //private int ERROR_MARGIN = 5;

    private float mTotalDistance;
    private Location mCurrentLocation;
    private int errorMargin;

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public LocationManager(Context context, long locationUpdateInterval, int errorMargin) {
        this.context = context;
        this.errorMargin = errorMargin;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(locationUpdateInterval);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException ex) {}
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) { }

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = mCurrentLocation == null ? location : mCurrentLocation;

        float delta;
        delta = mCurrentLocation.distanceTo(location);

        /*
        float[] results = new float[1];
        Location.distanceBetween(
                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                location.getLatitude(), location.getLongitude(), results);
        delta = results[0];
        */

        if (delta >= errorMargin) {
            mTotalDistance += delta;
            Toast.makeText(context,
                    "mTotalDistance == " + mTotalDistance, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,
                    "ERROR MARGIN", Toast.LENGTH_SHORT).show();
        }

        mCurrentLocation = location;
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    public float getTotalDistance() {
        return mTotalDistance;
    }

    public long getLocationUpdateInterval() {
        return mLocationRequest.getInterval();
    }

    public Location getCurrentLocation() {
        return new Location(mCurrentLocation);
    }

    public void setLocationUpdateInterval(long l) {
        mLocationRequest.setInterval(l);
    }

    public int setErrorMargin(int i) {
        return errorMargin = i;
    }
}
