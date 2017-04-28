package se.kth.projectarbor.project_arbor;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.*;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.List;

/**
 * Created by Lazar and Friends
 */

public class TreeTabFragment extends Fragment {


    View fragmentView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tree_tab_fragment, container, false);
        fragmentView = view;

        // IS_NEW
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getContext().getApplicationContext().registerReceiver(this.new DistanceReceiver(), filter);

        // Getting all the current values and precenting them on screen
        setupValues();


        // The user can toggle to either collect "distance" or not
        mWalk = (ToggleButton) view.findViewById(R.id.toggleButton);
        mWalk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Intent intent = new Intent(getActivity(), MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_START);
                    getContext().startService(intent);
                } else {
                    // The toggle is disabled
                    Intent intent = new Intent(getActivity(), MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_STOP);
                    getContext().startService(intent);
                }
            }
        });
        return view;

    }
    // Getting all the current values and precenting them on screen
    private void setupValues() {

        weatherView = (TextView) fragmentView.findViewById(R.id.tvWeather);
        tempView = (TextView) fragmentView.findViewById(R.id.tvTemp);
        hpView = (TextView) fragmentView.findViewById(R.id.tvHP);
        treeView = (TextView) fragmentView.findViewById(R.id.tvTree);
        distanceView = (TextView) fragmentView.findViewById(R.id.tvDistance);
        sunView = (TextView) fragmentView.findViewById(R.id.tvSun);
        waterView = (TextView) fragmentView.findViewById(R.id.tvWater);

        List<Object> list = DataManager.readState(getContext().getApplicationContext(), MainService.filename);

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
