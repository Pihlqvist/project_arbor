package se.kth.projectarbor.project_arbor;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;
/**
 * Created by Project Arbor on 2017-04-24.
 *
 *  This class is the main view of the tree and gives information about it.
 *  It is from this view that the user can stat a "Walk" to collect "distance"
 *  that is treated like a currency of sort in game.
 *
 */

public class TreeGame extends Activity {

    // Declaring all views and buttons
    private ToggleButton mWalk;
    private TextView weatherView;
    private TextView tempView;
    private TextView hpView;
    private TextView treeView;
    private TextView distanceView;
    private TextView sunView;
    private TextView waterView;

    // All objects used
    private Float distance;
    private Tree tree;
    private Environment environment;

    // IS_NEW
    private class DistanceReceiver extends BroadcastReceiver {
        private int oneUpdate = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                distanceView.setText("Distance: " + extras.getDouble("DISTANCE"));
            }


            if (intent.getAction().equals(MainService.TREE_DATA)) {
                weatherView.setText("Weather: " + environment.getWeather().toString());
                tempView.setText("Temp: " + environment.getTemp());
                hpView.setText("HP: " + extras.getInt("HP"));
                treeView.setText("Tree, Phase: " + extras.getString("PHASE"));
                sunView.setText("Sun Buffer: " + extras.getInt("SUN"));
                waterView.setText("Water Buffer: " + extras.getInt("WATER"));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_game);

        // IS_NEW
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getApplicationContext().registerReceiver(this.new DistanceReceiver(), filter);

        // Getting all the current values and precenting them on screen
        setupValues();


        // The user can toggle to either collect "distance" or not
        mWalk = (ToggleButton) findViewById(R.id.toggleButton);
        mWalk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Intent intent = new Intent(TreeGame.this, MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_START);
                    startService(intent);
                } else {
                    // The toggle is disabled
                    Intent intent = new Intent(TreeGame.this, MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_STOP);
                    startService(intent);
                }
            }
        });

    }

    // Getting all the current values and precenting them on screen
    private void setupValues() {
        weatherView = (TextView) findViewById(R.id.tvWeather);
        tempView = (TextView) findViewById(R.id.tvTemp);
        hpView = (TextView) findViewById(R.id.tvHP);
        treeView = (TextView) findViewById(R.id.tvTree);
        distanceView = (TextView) findViewById(R.id.tvDistance);
        sunView = (TextView) findViewById(R.id.tvSun);
        waterView = (TextView) findViewById(R.id.tvWater);

        List<Object> list = DataManager.readState(getApplicationContext(), MainService.filename);

        tree = (Tree) list.get(0);
        environment = (Environment) list.get(1);

        weatherView.setText("Weather: " + environment.getWeather().toString());
        tempView.setText("Temp: " + environment.getTemp());
        hpView.setText("HP: " + tree.getHealth());
        treeView.setText("Tree, Phase: " + tree.getTreePhase());
        sunView.setText("Sun Buffer: " + tree.getSunLevel());
        waterView.setText("Water Buffer: " + tree.getWaterLevel());

    }

}
