package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;

public class NewTreeActivity extends AppCompatActivity {

    private final static String TAG = "ARBOR_NEW_TREE";

    private Button newTreeBtn;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("FIRST_TREE", false)) {
            startService(new Intent(NewTreeActivity.this, MainService.class)
            .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME));
        }

        setContentView(R.layout.activity_new_tree);

        newTreeBtn = (Button) findViewById(R.id.new_tree_btn);
        newTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.saveState(getApplicationContext(), MainService.filename,
                        new Tree(), new Environment.Forecast[]{}, new Double(0));
                sharedPreferences.edit().putBoolean("FIRST_TREE", true).commit();
                Log.d(TAG, "new save state");

                Intent intent = new Intent(NewTreeActivity.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_NEED);
                PendingIntent pendingIntent = PendingIntent.getService(NewTreeActivity.this, 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (MainService.ALARM_HOUR * 1000), pendingIntent);

                Intent updateIntent = new Intent(NewTreeActivity.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME);
                startService(updateIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {}

}
