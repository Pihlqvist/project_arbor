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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mStartService;
    private Button mStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartService = (Button) findViewById(R.id.start_service_btn);
        mStartService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("MESSAGE_TYPE", LocationManager.MSG_START_RUN);
                    intent.putExtras(bundle);
                    startService(intent);
                }
            }
        );

        mStopService = (Button) findViewById(R.id.stop_service_btn);
        mStopService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("MESSAGE_TYPE", LocationManager.MSG_STOP_RUN);
                    intent.putExtras(bundle);
                    startService(intent);
                }
            }
        );
    }
}
