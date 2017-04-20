package se.kth.projectarbor.project_arbor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Methods to bind to MainService
    boolean isBound;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            MainService.ServiceBinder binder = (MainService.ServiceBinder) service;
            MainService = binder.getServiceInstance();
            isBound = true;
        }
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    protected void onStop() {
        super.onStop();
        if (isBound){
            unbindService(serviceConnection);
        }
    }
}
