package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fredrik Pihlqvist on 2017-04-28.
 */

public class TreeTab extends Fragment {

    private final static String TAG = "ARBOR_TREE_TAB";
    private static int testInt;

    // Declaring all views and buttons
    private ToggleButton walkBtn;
    private TextView weatherView;
    private TextView tempView;
    private TextView hpView;
    private TextView treeView;
    private TextView distanceView;
    private TextView sunView;
    private TextView waterView;
    private View view;

    private ViewGroup vgClouds;
    private ViewGroup vgRain;
    private ViewGroup vgSun;



    private Environment.Weather weather;

    private SharedPreferences sharedPreferences;


    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            Log.d(TAG, "onReceive()");

            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                distanceView.setText("Distance: " + extras.getDouble("DISTANCE"));
            }

            if (intent.getAction().equals(MainService.TREE_DATA)) {
                tempView.setText("Temp: " + extras.getDouble("TEMP"));
                weatherView.setText("Weather: " + extras.getString("WEATHER"));
                if (extras.getInt("HP") < 1) {
                    hpView.setText("HP: DEAD");
                } else {
                    hpView.setText("HP: " + extras.getInt("HP"));
                }
                treeView.setText("Tree, Phase: " + extras.getString("PHASE"));
                sunView.setText("Sun Buffer: " + extras.getInt("SUN"));
                waterView.setText("Water Buffer: " + extras.getInt("WATER"));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView in tree tab");

        this.view = inflater.inflate(R.layout.fragment_tree_tab, container, false);

        setupValues();

        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);

        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor"
                , MODE_PRIVATE);


        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            hpView.setText("HP: " + extras.getInt("HP"));
            treeView.setText("Tree, Phase: " + extras.getString("PHASE"));
            sunView.setText("Sun Buffer: " + extras.getInt("SUN"));
            waterView.setText("Water Buffer: " + extras.getInt("WATER"));
            tempView.setText("Temp: " + extras.getDouble("TEMP"));
            weatherView.setText("Weather: " + extras.get("WEATHER").toString());
            weather = (Environment.Weather) extras.get("WEATHER");
        }


        // changeing weahter view depending on "testInt"
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.treefragmentlayout);
        testInt = 1;
        switch (testInt) {
            case 1:
                CloudView cloudView = new CloudView(getContext());
                layout = cloudView.addViews(layout);
                break;
            case 2:
                SunView sunView = new SunView(getActivity());
                layout = (RelativeLayout) sunView.addViews(layout);
                break;
            case 3:
                RainView rainView = new RainView(getActivity());
                layout = (RelativeLayout) rainView.addViews(layout);
                break;
            default:
                Log.d(TAG, "no case in weather switch");
        }


        view = layout;

        // The user can toggle to either collect "distance" or not
        walkBtn = (ToggleButton) view.findViewById(R.id.toggleButton);
        if (sharedPreferences.contains("TOGGLE")) {
            walkBtn.setChecked(sharedPreferences.getBoolean("TOGGLE", false));
        }
        walkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Intent intent = new Intent(getActivity(), MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_START);
                    getActivity().startService(intent);
                } else {
                    // The toggle is disabled
                    Intent intent = new Intent(getActivity(), MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_STOP);
                    getActivity().startService(intent);
                }

                sharedPreferences.edit().putBoolean("TOGGLE", isChecked).apply();

            }
        });



        return view;
    }


    // Setup all the views
    private void setupValues() {
        weatherView = (TextView) view.findViewById(R.id.tvWeather);
        tempView = (TextView) view.findViewById(R.id.tvTemp);
        hpView = (TextView) view.findViewById(R.id.tvHP);
        treeView = (TextView) view.findViewById(R.id.tvTree);
        distanceView = (TextView) view.findViewById(R.id.tvDistance);
        sunView = (TextView) view.findViewById(R.id.tvSun);
        waterView = (TextView) view.findViewById(R.id.tvWater);
    }

    @Override
    public boolean getUserVisibleHint() {
        Log.d(TAG, "getUserVisibleHint()");
        return super.getUserVisibleHint();
    }
}

