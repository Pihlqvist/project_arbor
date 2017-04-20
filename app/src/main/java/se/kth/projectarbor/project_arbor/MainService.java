package se.kth.projectarbor.project_arbor;

import android.app.Service;
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

public class MainService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    final static int MSG_START_RUN = 0;
    final static int MSG_STOP_RUN = 1;

    private static long LOCATION_UPDATE_INTERVAL = 10000;
    private static int ERROR_MARGIN = 5;

    private ServiceHandler handler;
    private float mTotalDistance;
    private Location mCurrentLocation;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_RUN:
                    Toast.makeText(getApplicationContext(),
                            "Service is tracking your activity", Toast.LENGTH_SHORT).show();
                    mGoogleApiClient.connect();
                    break;
                case MSG_STOP_RUN:
                    LocationServices.FusedLocationApi.removeLocationUpdates(
                            MainService.this.mGoogleApiClient, MainService.this);
                    MainService.this.mGoogleApiClient.disconnect();
                    Toast.makeText(getApplicationContext(),
                            "Service has stopped tracking your activity", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "Service is starting...", Toast.LENGTH_SHORT).show();

        HandlerThread thread = new HandlerThread("StartedService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        handler = new ServiceHandler(looper);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = handler.obtainMessage();
        msg.arg1 = startId;
        msg.what = intent.getExtras().getInt("MESSAGE_TYPE");

        handler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Connection Success", Toast.LENGTH_LONG).show();

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
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();

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

        if (delta >= ERROR_MARGIN) {
            mTotalDistance += delta;
            Toast.makeText(this, "mTotalDistance == " + mTotalDistance, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "ERROR MARGIN", Toast.LENGTH_LONG).show();
        }

        mCurrentLocation = location;
    }
}

// if (startService(intent) == null) return;