package se.kth.projectarbor.project_arbor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    MainService mainService;

    Button mStartService;
    Button mStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, MainService.class);
        //Bundle bundle = new Bundle();
        //bundle.putInt("MESSAGE_TYPE", MainService.MSG_START_RUN);
        //intent.putExtras(bundle);
        //startService(intent);

        mStartService = (Button) findViewById(R.id.start_service_btn);
        mStartService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }
        );

        mStopService = (Button) findViewById(R.id.stop_service_btn);
        mStopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }
        );
    }

    //Methods to bind to MainService
    boolean isBound;

    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.ServiceBinder binder = (MainService.ServiceBinder) service;
            mainService = binder.getServiceInstance();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound){
            unbindService(serviceConnection);
        }
    }
}
