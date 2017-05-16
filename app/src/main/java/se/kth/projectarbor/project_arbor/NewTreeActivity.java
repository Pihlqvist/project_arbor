package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.RelativeLayout;

import se.kth.projectarbor.project_arbor.view_objects.NewTreeClouds;
import se.kth.projectarbor.project_arbor.weather.Environment;

/*
* Created by Project Arbor
*
* This activity launches with the application, it determines if a tree is alive
* if the tree is alive the game will start. If this is the first time the user
* launches the game a "new tree" button will be presented
*
* This activity resumes the game if it can, if not it will create all the
* necessary objects. It will also start the game logic with an alarm.
*
 */

public class NewTreeActivity extends AppCompatActivity  {

    private final static String TAG = "ARBOR_NEW_TREE";
    private Button newTreeBtn;
    SharedPreferences sharedPreferences = null;
    private NewTreeClouds newTreeClouds;
    private RelativeLayout cloudlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Controls if a tree exist or not, will go to main activity if the tree exist.
        // If a tree dose not exist it will set up the new tree view.
        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("FIRST_TREE", false)) {
            startService(new Intent(NewTreeActivity.this, MainService.class)
            .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME));
        }

        // TODO: Check if tree is alive or not. If not, show death screen.

        setContentView(R.layout.activity_new_tree);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.new_tree_layout);
        getClouds();
        layout.addView(cloudlayout);
        setContentView(layout);

        if (!isNetworkAvailable()) {
            displayPromptForEnablingInternet();
        }

        // If the user press this button we will save a new game state to the installation
        // folder and start the game logic and go to the main activity view.
        newTreeBtn = (Button) findViewById(R.id.new_tree_btn);
        newTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Make new tree and game settings
                DataManager.saveState(getApplicationContext(), MainService.filename,
                        new Tree(), new Environment.Forecast[]{}, 0.0, 0);
                sharedPreferences.edit().putBoolean("FIRST_TREE", true).apply();
                Log.d(TAG, "new save state");

                sharedPreferences.edit().putLong("TREE_START_TIME", System.currentTimeMillis()).apply();

                // Start game storage
                Intent intent = new Intent(NewTreeActivity.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_NEED);
                PendingIntent pendingIntent = PendingIntent.getService(NewTreeActivity.this, 0, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + (MainService.ALARM_HOUR * 1000), pendingIntent);

                // Start Game
                Intent updateIntent = new Intent(NewTreeActivity.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME);
                startService(updateIntent);
            }
        });

    }
    private void getClouds(){
        RelativeLayout layout = new RelativeLayout(getApplicationContext());
        newTreeClouds = new NewTreeClouds(getApplicationContext());
        layout = newTreeClouds.addViews(layout);
        cloudlayout = layout;
    }

    // check whether there is internet connection or wifi connection
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return  true;
        } else {
            return false;
        }

    }

    //Display a prompt which goes to internet setting if the user click ok
    public void displayPromptForEnablingInternet() {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        final String action = Settings.ACTION_WIRELESS_SETTINGS;
        final String message = "Do you want open Internet Setting?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();

    }
}
