package se.kth.projectarbor.project_arbor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
 * Created by Project Arbor on 2017-04-24.
 *
 * This is the start page of the first iteration of the game "Arbor". This Activity
 * controls the creation of a "Tree" ( the main game object ) and gives the player
 * a choice in making a new tree or resuming the same tree. Only one tree can be
 * active at one time.
 *
 */

public class MainActivity extends Activity {

    // Declaring buttons used later
    private Button mNewTree;
    private Button mResume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This will create a new tree and start the game logic, it will then
        // take the user to the main view of the tree
        mNewTree = (Button) findViewById(R.id.start_button);
        mNewTree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.createUser(getApplicationContext(), MainService.filename);

                    Intent intent = new Intent(MainActivity.this, MainService.class)
                            .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_NEED);
                    PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + (MainService.ALARM_HOUR * 1000), pendingIntent);

                    Intent updateIntent = new Intent(MainActivity.this, MainService.class)
                            .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_VIEW);
                    startService(updateIntent);

                    startActivity(new Intent(MainActivity.this, TreeGame.class));
                }
        });

        // This will continue a game state and take the user to the main view of the tree
        mResume = (Button) findViewById(R.id.resume_button);
        mResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TreeGame.class));
            }
        });


    }

}
