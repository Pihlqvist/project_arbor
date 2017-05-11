package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import se.kth.projectarbor.project_arbor.view_objects.CloudView;
import se.kth.projectarbor.project_arbor.view_objects.RainView;
import se.kth.projectarbor.project_arbor.view_objects.SunView;
import se.kth.projectarbor.project_arbor.weather.Environment;

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
    private ImageView ivTree;


    private RelativeLayout weatherLayout;
    private Environment.Weather weather;
    private SharedPreferences sharedPreferences;

    private int currentPhase;
    private int newPhase;

//TODO:Fix messages (Ramcin)
    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Log.d(TAG, "onReceive()");
            if (intent.getAction().equals("WEATHER_DATA")) {
                // Build new weather layout depending on weather
                weather = (Environment.Weather) extras.get("WEATHER");
                RelativeLayout layout = (RelativeLayout) view;
                layout.removeView(weatherLayout);
                setWeatherLayout();
                layout.addView(weatherLayout);
                view = layout;
            } else if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                stepView.setText(String.format("Steps: %d", extras.getInt("STEPCOUNT")));
                distanceView.setText(String.format("Distance: %.2f m",extras.getDouble("DISTANCE")));
            } else if (intent.getAction().equals(MainService.TREE_DATA)) {
                Log.d(TAG, "TREE_DATA");
                newPhase = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();
                if (newPhase != currentPhase) {
                    setTreePhase(newPhase);
                }

                // TODO: Here only becuse WEATHER_DATA is not done (Fredrik)
                weather = (Environment.Weather) extras.get("WEATHER");
                RelativeLayout layout = (RelativeLayout) view;
                layout.removeView(weatherLayout);
                setWeatherLayout();
                layout.addView(weatherLayout);
                view = layout;
            }

            if (intent.getAction().equals(MainService.WEATHER_DATA)) {
                Log.d("ARBOR_WEATHER", "Broadcast received");
                tempView.setText("Temp: " + extras.getDouble("TEMP", 0));
                weatherView.setText("Weather: " + extras.getString("WEATHER", null));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tree_tab, container, false);

        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction("WEATHER_DATA");  // TODO: Implement in Environment (Fredrik)
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);


        sharedPreferences = getActivity().getSharedPreferences(
                "se.kth.projectarbor.project_arbor", MODE_PRIVATE);

        // looks for the last used phase number
        if (sharedPreferences.contains("CURRENT_TREE_PHASE")) {
            currentPhase = sharedPreferences.getInt("CURRENT_TREE_PHASE", 1);
        } else {
            sharedPreferences.edit().putInt("CURRENT_TREE_PHASE", 1).apply();
            currentPhase = 1;
        }

        // Setup Views
        treeView = (TextView) view.findViewById(R.id.tvTree);
        ivTree = (ImageView) view.findViewById(R.id.treeButton);
        distanceView = (TextView) view.findViewById(R.id.tvDistance);
        stepView = (TextView) view.findViewById(R.id.tvStepCount);

        // Pick the right tree depending on the current Phase
        setTreePhase(currentPhase);

        // Get first information about weather
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            treeView.setText("Tree, Phase: " + extras.getString("PHASE"));
            newPhase = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();
            weather = (Environment.Weather) extras.get("WEATHER");
        } else {
            weather = Environment.Weather.CLOUDY;
            Log.e(TAG, "Weather could not be found");
            // TODO: from foreground, bring info about the weather in a Intent
        }

        // If the tree's phase changed it will start an animation if you press it
        if (currentPhase < newPhase) {
            //treePhaseChange();
        }

        // Change weather view depending on "weather"
        weatherLayout = new RelativeLayout(getContext());
        setWeatherLayout();
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "RESUME");

        // Remember toggle button state
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

    }

    // Applying the right weather layout depending on IRL weather
    private void setWeatherLayout() {
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
            // TODO: Fix later when its implemented in Environment (Fredrik)
            /*case CLOUDYSUN:
                SunView sunView1 = new SunView(getActivity());
                CloudView cloudView1 = new CloudView(getContext());
                layout = cloudView1.addViews(sunView1.addViews(layout));  */
            default:
                Log.d(TAG, "no case in weather switch");
        }
        weatherLayout = layout;
    }

    // Shows the right tree
    private void setTreePhase(int phaseNumber) {
        Log.d(TAG, "setTreePhase");
        switch (phaseNumber) {
            case 1:
                ivTree = (ImageView) view.findViewById(R.id.treeButton);
                ivTree.setImageResource(R.drawable.seed_to_sprout_01);
                Log.d(TAG, "ivTree seed");
                break;
            case 2:
                ivTree = (ImageView) view.findViewById(R.id.treeButton);
                ivTree.setImageResource(R.drawable.sprout_to_sapling_01);
                Log.d(TAG, "ivTree sprout");
                break;
            case 3:
                ivTree = (ImageView) view.findViewById(R.id.treeButton);
                ivTree.setImageResource(R.drawable.sprout_to_sapling_29);
                Log.d(TAG, "ivTree sapling");
                break;
        }
    }

    // TODO: FIX BACKGROUND RESOURCE, CURRENTLY USING ALOT OF MEMORY
    private void treePhaseChange() {
        switch (newPhase) {
            case 2:
                ivTree.setBackgroundResource(R.drawable.anim_seed_to_sprout);
                ivTree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimationDrawable frameAnim = (AnimationDrawable) ivTree.getBackground();
                        frameAnim.start();
                        currentPhase = newPhase;
                    }
                });
                break;

            case 3:
                ivTree.setBackgroundResource(R.drawable.grow_sprout_to_sapling);
                ivTree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AnimationDrawable frameAnim = (AnimationDrawable) ivTree.getBackground();
                        frameAnim.start();
                        currentPhase = newPhase;
                    }
                });
                break;
        }
    }

}
