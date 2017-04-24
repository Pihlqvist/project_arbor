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
import android.util.Log;

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

    private float mTotalDistance;
    private Location mCurrentLocation;
    private int mErrorMargin;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public LocationManager(Context context, long locationUpdateInterval, int errorMargin) {
        this.mContext = context;
        this.mErrorMargin = errorMargin;

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

        if (delta >= mErrorMargin) {
            mTotalDistance += delta;
            Log.d(MainService.TAG, "mTotalDistance == " + mTotalDistance);
        } else {
            Log.d(MainService.TAG, "ERROR MARGIN");
        }

        mCurrentLocation = location;
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
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

    protected void setTotalDistance(float d) {
        mTotalDistance = d;
    }

    public void setLocationUpdateInterval(long l) {
        mLocationRequest.setInterval(l);
    }

    public int setErrorMargin(int i) {
        int old = mErrorMargin;
        mErrorMargin = i;
        return old;
    }
}
