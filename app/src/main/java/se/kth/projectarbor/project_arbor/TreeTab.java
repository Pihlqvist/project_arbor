package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fredrik Pihlqvist on 2017-04-28.
 */

public class TreeTab extends Fragment {

    private final static String TAG = "ARBOR_TREE_TAB";

    // Declaring all views and buttons
    private ToggleButton walkBtn;
    private TextView treeView;
    private View view;

    private SunView sunView;
    private RainView rainView;
    private CloudView cloudView;

    private TextView distanceView;
    private TextView stepView;

    private RelativeLayout weatherLayout;

    private Environment.Weather weather;

    private SharedPreferences sharedPreferences;

    private double mDistance;
    private int mStep;

    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Log.d(TAG, "onReceive()");
            if (intent.getAction().equals("WEATHER_DATA")) {
                // Build new weatherLayout depending on weather
                weather = (Environment.Weather) extras.get("WEATHER");
                RelativeLayout layout = (RelativeLayout) view;
                layout.removeView(weatherLayout);
                setWeahterLayout();
                layout.addView(weatherLayout);
                view = layout;
            }
            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                mDistance = extras.getDouble("DISTANCE");
                mStep = extras.getInt("STEPCOUNT");
            }

            statsDisplay();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //distanceView = (TextView) view.findViewById(R.id.tvDistance);
        //stepView = (TextView) view.findViewById(R.id.tvStepCount);
        Log.d(TAG, "onCreateView in tree tab");

        this.view = inflater.inflate(R.layout.fragment_tree_tab, container, false);

        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction("WEATHER_DATA");
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        getActivity().registerReceiver(this.new Receiver(), filter);

        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor"
                , MODE_PRIVATE);

        treeView = (TextView) view.findViewById(R.id.tvTree);

        // Get first information about weather
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            treeView.setText("Tree, Phase: " + extras.getString("PHASE"));
            weather = (Environment.Weather) extras.get("WEATHER");
        }


        // Change weather view depending on "weather"
        weatherLayout = new RelativeLayout(getContext());
        switch (weather) {
            case CLOUDY:
                CloudView cloudView = new CloudView(getContext());
                weatherLayout = cloudView.addViews(weatherLayout);
                break;
            case SUN:
                SunView sunView = new SunView(getActivity());
                weatherLayout = (RelativeLayout) sunView.addViews(weatherLayout);
                break;
            case RAIN:
                RainView rainView = new RainView(getActivity());
                weatherLayout = (RelativeLayout) rainView.addViews(weatherLayout);
                break;
            /*case CLOUDYSUN:
                SunView sunView1 = new SunView(getActivity());
                CloudView cloudView1 = new CloudView(getContext());
                weatherLayout = cloudView1.addViews((RelativeLayout)sunView1.addViews(weatherLayout)); */
            default:
                Log.d(TAG, "no case in weather switch");
        }

        RelativeLayout currentLayout = (RelativeLayout) view.findViewById(R.id.treefragmentlayout);
        currentLayout.addView(weatherLayout);
        view = currentLayout;


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
        Button setWheaterBtn = (Button) view.findViewById(R.id.setWeather);
        setWheaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcast = new Intent();
                broadcast.setAction("WEATHER_DATA");
                broadcast.putExtra("WEATHER", Environment.Weather.CLOUDY);
                getContext().sendBroadcast(broadcast);
            }
        });

        Button setWheaterBtn2 = (Button) view.findViewById(R.id.setWeather2);
        setWheaterBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent broadcast = new Intent();
                broadcast.setAction("WEATHER_DATA");
                broadcast.putExtra("WEATHER", Environment.Weather.RAIN);
                getContext().sendBroadcast(broadcast);
            }
        });


        return view;
    }
        @Override
        public void onResume() {
            super.onResume();

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
        //   statsDisplay();

        }
    private void setWeahterLayout() {
        RelativeLayout layout = new RelativeLayout(getContext());
        switch (weather) {
            case CLOUDY:
                cloudView = new CloudView(getContext());;
                layout = cloudView.addViews(layout);
                break;
            case SUN:
                sunView = new SunView(getActivity());
                layout = (RelativeLayout) sunView.addViews(layout);
                break;
            case RAIN:
                rainView = new RainView(getActivity());
                layout = (RelativeLayout) rainView.addViews(layout);
                break;
            /*case CLOUDYSUN:
                SunView sunView1 = new SunView(getActivity());
                CloudView cloudView1 = new CloudView(getContext());
                layout = cloudView1.addViews(sunView1.addViews(layout));  */
            default:
                Log.d(TAG, "no case in weather switch");
        }

        weatherLayout = layout;
    }
    private void statsDisplay(){
        if(!(mDistance == -1)) {
       //     distanceView.setText("Distance: " + mDistance);
        }else{
        //    distanceView.setText("WTU"); //WTU walk to update
        }
        if(!(mStep == -1)) {
            stepView.setText("StepCount " + mStep);
        }else{
            stepView.setText("WTU"); //WTU walk to update
        }
    }
}

