package se.kth.projectarbor.project_arbor;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private Button mStart;
    private Button mStop;
    private Button mCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mStart = (Button) findViewById(R.id.start_btn);
        mStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("MESSAGE_TYPE", MainService.MSG_START);
                    intent.putExtras(bundle);
                    startService(intent);
                }
            }
        );

        mStop = (Button) findViewById(R.id.stop_btn);
        mStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("MESSAGE_TYPE", MainService.MSG_STOP);
                    intent.putExtras(bundle);
                    startService(intent);
                }
            }
        );

        mCreate = (Button) findViewById(R.id.create_btn);
        mCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainService.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("MESSAGE_TYPE", MainService.MSG_CREATE);
                    intent.putExtras(bundle);
                    DataManager.createUser(getApplicationContext(), MainService.filename);
                    startService(intent);
                }
            }
        );

    }
   public void  goTillTree (View view){
       Intent intent = new Intent(this, TreeGame.class);
       startActivity(intent);
   }
}
