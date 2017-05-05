package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fredrik Pihlqvist on 2017-04-28.
 */

public class TreeTab extends Fragment {

    private final static String TAG = "ARBOR_TREE_TAB";

    // Declaring all views and buttons
    private ToggleButton walkBtn;
    private TextView weatherView;
    private TextView tempView;
    private TextView hpView;
    private TextView treeView;
    private TextView distanceView;
    private TextView stepView;
    private TextView sunView;
    private TextView waterView;
    private TextView totalDistanceView;
    private View view;

    private String mWeather;
    private double mTemp;
    private int mHP;
    private double mDistance;
    private String mPhase;
    private int mStep;
    private int mSun;
    private int mWater;
    private double mTotalDistance;

    private SharedPreferences sharedPreferences;


    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            Log.d(TAG, "onReceive()");

            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                mDistance = extras.getDouble("DISTANCE");
                mStep = extras.getInt("STEPCOUNT");
            }

            if (intent.getAction().equals(MainService.TREE_DATA)) {
                mTemp = extras.getDouble("TEMP");
                mWeather = extras.getString("WEATHER");
                mHP = extras.getInt("HP");
                mPhase = extras.getString("PHASE");
                mSun = extras.getInt("SUN");
                mWater = extras.getInt("WATER");
                mTotalDistance = extras.getDouble("TOTALKM");
            }
            statsDisplay();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("TAG", "onCreateView in tree tab");

        this.view = inflater.inflate(R.layout.fragment_tree_tab, container, false);

        setupValues(); //Sets instances

        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);

        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor"
                , MODE_PRIVATE);

        // The user can toggle to either collect "distance" or not
        walkBtn = (ToggleButton) view.findViewById(R.id.toggleButton);
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



        return this.view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mHP = extras.getInt("HP");
            mPhase = extras.getString("PHASE");
            mSun = extras.getInt("SUN");
            mWater = extras.getInt("WATER");
            mTemp = extras.getDouble("TEMP");
            mWeather = extras.getString("WEATHER");
            mTotalDistance = extras.getDouble("TOTALKM");
            statsDisplay();
        }
        mStep = -1;
        mDistance = -1;
        Log.d("TAG", "RESUME");
        if (sharedPreferences.contains("TOGGLE")) {
            walkBtn.setChecked(sharedPreferences.getBoolean("TOGGLE", false));
            if(sharedPreferences.getBoolean("TOGGLE", false)) {
                Intent intent2 = new Intent(getActivity(), MainService.class);
                intent2.putExtra("MESSAGE_TYPE", MainService.MSG_RESUME_HEAVY);
                getActivity().startService(intent2);
            }else{
                Intent intent3 = new Intent(getActivity(), MainService.class);
                intent3.putExtra("MESSAGE_TYPE", MainService.MSG_RESUME_LIGHT);
                getActivity().startService(intent3);
            }
        }
        statsDisplay();

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
        stepView = (TextView) view.findViewById(R.id.tvStepCount);
        totalDistanceView = (TextView) view.findViewById(R.id.tvTotalDistance);
    }
    private void conCurrencySimultator(){
    }
    private void statsDisplay(){
        if(!(mDistance == -1)) {
            distanceView.setText("Distance: " + mDistance);
        }else{
            distanceView.setText("WTU"); //WTU walk to update
        }
        if(!(mStep == -1)) {
            stepView.setText("StepCount " + mStep);
        }else{
            stepView.setText("WTU"); //WTU walk to update
        }
        tempView.setText("Temp: " + mTemp);
        weatherView.setText("Weather: " + mWeather);
        if (mHP < 1)
            hpView.setText("HP < 1");
        else
            hpView.setText("HP: " + mHP);
        treeView.setText("Tree, Phase: " + mPhase);
        sunView.setText("Sun Buffer: " + mSun);
        waterView.setText("Water Buffer: " + mWater);
        totalDistanceView.setText("Total Distance: " + (mTotalDistance + mDistance));
    }
}
