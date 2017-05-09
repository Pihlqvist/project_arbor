package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.view.View;

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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Controls if a tree exist or not, will go to main activity if the tree exist.
        // If a tree dose not exist it will set up the tree view.
        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("FIRST_TREE", false)) {
            startService(new Intent(NewTreeActivity.this, MainService.class)
            .putExtra("MESSAGE_TYPE", MainService.MSG_TREE_GAME));
        }

        setContentView(R.layout.activity_new_tree);

        // If the user press this button we will save a new game state to the installation
        // folder and start the game logic and go to the main activity view.
        newTreeBtn = (Button) findViewById(R.id.new_tree_btn);
        newTreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isNetworkAvailable()){
                    displayMessageForEnablingInternetOrGps(Settings.ACTION_WIRELESS_SETTINGS);
                }

                else if (!isGpsAvailable()){
                        displayMessageForEnablingInternetOrGps(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                }

                else{
                DataManager.saveState(getApplicationContext(), MainService.filename,
                        new Tree(), new Environment.Forecast[]{}, new Double(0), (int) 0);
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
            }
        });
    }

    // check whether there is internet connection or wifi connection
    private  boolean  isNetworkAvailable( ) {

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return  true;
        }
        else
          return false;
    }

    private boolean isGpsAvailable (){
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            return true;
        }
        else{
            return false;
        }
    }

    //Display a prompt which goes to internet setting if the user click ok
    public void displayMessageForEnablingInternetOrGps(final String action)
    {
        final String message;
        final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        if (action.equals (Settings.ACTION_WIRELESS_SETTINGS)) {
             message = "Do you want to open Internet Setting?";
        }
        else  {
        message = "Do you want to open GPS Setting?";
    }

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
