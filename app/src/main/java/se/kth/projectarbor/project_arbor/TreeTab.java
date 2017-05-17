package se.kth.projectarbor.project_arbor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

import com.google.android.gms.ads.formats.NativeAd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import se.kth.projectarbor.project_arbor.view_objects.CloudView;
import se.kth.projectarbor.project_arbor.view_objects.RainView;
import se.kth.projectarbor.project_arbor.view_objects.SunView;
import se.kth.projectarbor.project_arbor.view_objects.CloudSunView;
import se.kth.projectarbor.project_arbor.view_objects.TreeView;
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
    private TextView tempView;
    private TextView distanceTextView;
    private TextView stepTextView;
    private ImageView ivTree;
    private View sessionView;
    private TextView tvPollen;
    private TreeView treeAnimView;
    private Animation animAppear;
    private Animation animDisappear;


    private RelativeLayout weatherLayout;
    private Environment.Weather weather;
    private SharedPreferences sharedPreferences;

    int currentPhase;
    int newPhase;


    /* // TODO: See so it works after integrish
    //TODO:Fix messages (Ramcin)
    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Log.d(TAG, "onReceive()");
            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                stepTextView.setText(String.format("%d", extras.getInt("STEPCOUNT")));
                distanceTextView.setText(String.format("%.2f km",extras.getDouble("DISTANCE")/1000));
            } else if (intent.getAction().equals(MainService.TREE_DATA)) {
                Log.d(TAG, "TREE_DATA");
                newPhase = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();
                if (newPhase != currentPhase) {
                    setTreePhase(newPhase);
                }
            } else if (intent.getAction().equals(MainService.WEATHER_DATA)) {
                Log.d("ARBOR_WEATHER", "Broadcast received");
                // Build new weather layout depending on weather
                Environment.Weather newWeather = (Environment.Weather) extras.get("WEATHER");
                if (true) { // TODO: change to goodie (Fredrik)
                    weather = newWeather;
                    RelativeLayout layout = (RelativeLayout) view;
                    layout.removeView(weatherLayout);
                    setWeatherLayout();
                    layout.addView(weatherLayout);
                    view = layout;
                }
            }


        }
    }
    */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tree_tab, container, false);
        Log.d("ARBOR", "Oncreate");
        /* // TODO: See if it works after integration
        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainService.WEATHER_DATA);
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new Receiver(), filter);
        */
        //Get tree phase critical for treeview
        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor"
                , MODE_PRIVATE);

        // looks for the last used phase number
        if (sharedPreferences.contains("CURRENT_TREE_PHASE")) {
            currentPhase = sharedPreferences.getInt("CURRENT_TREE_PHASE", 1);
        } else {
            sharedPreferences.edit().putInt("CURRENT_TREE_PHASE", 1).apply();
            currentPhase = 1;
        }

        //Here Patrik did a construct for the treeview; treeview is a special extention of ImageView to fit our use

        InputStream inputStream = null;
        try{
            inputStream = getActivity().getAssets().open("tree_life_cycle.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        treeAnimView = new TreeView(getActivity(), currentPhase);
        Log.d("PATRIK", "" + currentPhase);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            buffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        treeAnimView.setBytes(buffer.toByteArray());

        // Get first information about weather
        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        // Change weather view depending on "weather"
        weatherLayout = new RelativeLayout(getContext());
        RelativeLayout currentLayout = (RelativeLayout) view.findViewById(R.id.treefragmentlayout);
        currentLayout.addView(weatherLayout);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(((size.x/100)*10), size.y/2-20*(size.y/100), 10*(size.x/100), 32*(size.y/100));//left, top, right, bottom
        currentLayout.addView(treeAnimView, lp);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        currentLayout.addView(getActivity().getLayoutInflater().inflate(R.layout.grass_tuva_layout, null), lp2);

        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        currentLayout.addView(getActivity().getLayoutInflater().inflate(R.layout.view_resource_session, null), lp3);
        view = currentLayout;
        // Setup Views
        treeView = (TextView) view.findViewById(R.id.tvTree);
        ivTree = (ImageView) view.findViewById(R.id.treeButton);
        distanceTextView = (TextView) view.findViewById(R.id.tvDistance);
        stepTextView = (TextView) view.findViewById(R.id.tvStepCount);
        sessionView = view.findViewById(R.id.sessionView);
        sessionView.setVisibility(View.GONE);
        tvPollen = (TextView) view.findViewById(R.id.golden_pollen);
        tvPollen.setText("" + MainUIActivity.goldenPollen);
        tempView = (TextView) view.findViewById(R.id.tempTV);
        // Sends message to MainService and asks for weather
        if (weather == null) {
            getActivity().startService(new Intent(getActivity(), MainService.class)
                    .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_WEATHER_VIEW));
        }
        if (extras != null) {
            treeView.setText("Tree, Phase: " + ((Tree.Phase) extras.get("PHASE")).getPhaseName());
            newPhase = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();

            distanceTextView.setText(String.format("%.2f km", extras.getDouble("SESSION_DISTANCE")));
            stepTextView.setText(String.format("%d", extras.getInt("SESSION_STEPS")));
        }
        // Animations for session
        animAppear = AnimationUtils.loadAnimation(getContext(), R.anim.session_appear);
        animDisappear = AnimationUtils.loadAnimation(getContext(), R.anim.session_disappear);
        animDisappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                sessionView.setVisibility(View.GONE);
                distanceTextView.setText("0 km");
                stepTextView.setText("0");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // The user can toggle to either collect "distance" or not
        walkBtn = (ToggleButton) view.findViewById(R.id.toggleButton);
        walkBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                    // Animate session View
                    sessionView.setVisibility(View.VISIBLE);
                    sessionView.startAnimation(animAppear);

                    Intent intent = new Intent(getActivity(), MainService.class);
                    intent.putExtra("MESSAGE_TYPE", MainService.MSG_START);
                    getActivity().startService(intent);
                } else {
                    // The toggle is disabled

                    // Animate session View
                    sessionView.startAnimation(animDisappear);

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
//            if(sharedPreferences.getBoolean("TOGGLE", false)) {
//                Intent intent2 = new Intent(getActivity(), MainService.class);
//                // TODO: Was "MSG_RESUME_HEAVY" before. Dint update stats tab correctly (Fredrik)
//                intent2.putExtra("MESSAGE_TYPE", MainService.MSG_RESUME_LIGHT);
//                getActivity().startService(intent2);
//            }else{
//                Intent intent3 = new Intent(getActivity(), MainService.class);
//                intent3.putExtra("MESSAGE_TYPE", MainService.MSG_RESUME_LIGHT);
//                getActivity().startService(intent3);
//            }
        }

    }

    // Applying the right weather layout depending on IRL weather
    void setWeatherLayout() {
        RelativeLayout layout = new RelativeLayout(getContext());
        View viewStat = ((MainUIActivity)getActivity()).statsTab.getView();
        View viewTree = ((MainUIActivity)getActivity()).treeTab.getView();
        View viewShop = ((MainUIActivity)getActivity()).shopTab.getView();
        switch (weather) {
            case CLOUDY:
                cloudView = new CloudView(getContext());
                layout = cloudView.addViews(layout);
                viewStat.setBackgroundResource(R.drawable.cloudy_background_2);
                viewTree.setBackgroundResource(R.drawable.cloudy_background_1);
                viewShop.setBackgroundResource(R.drawable.cloudy_background_3);
                break;
            case SUN:
                sunView = new SunView(getActivity());
                layout = (RelativeLayout) sunView.addViews(layout);
                viewStat.setBackgroundResource(R.drawable.blue_background_2);
                viewTree.setBackgroundResource(R.drawable.blue_background_1);
                viewShop.setBackgroundResource(R.drawable.blue_background_3);
                break;
            case RAIN:
                rainView = new RainView(getActivity());
                layout = (RelativeLayout) rainView.addViews(layout);
                viewStat.setBackgroundResource(R.drawable.rain_background_2);
                viewTree.setBackgroundResource(R.drawable.rain_background_1);
                viewShop.setBackgroundResource(R.drawable.rain_background_3);
                break;

            // TODO: Fix later when its implemented in Environment (Fredrik)
            // TODO: Does it work as intended?
            case PARTLY_CLOUDY:
                SunView sunView = new SunView(getActivity());
                CloudSunView cloudSunView = new CloudSunView(getContext());
                layout = cloudSunView.addViews((RelativeLayout) sunView.addViews(layout));
                viewStat.setBackgroundResource(R.drawable.blue_background_2);
                viewTree.setBackgroundResource(R.drawable.blue_background_1);
                viewShop.setBackgroundResource(R.drawable.blue_background_3);
                break;
            default:
                Log.d(TAG, "no case in weather switch");
        }

        weatherLayout = layout;
    }

    Environment.Weather getWeather() {
        if (weather == null)
            return Environment.Weather.NOT_AVAILABLE;
        else
            return weather;
    }

    // TODO: Fix the names
    void setWeather(Environment.Weather newWeather) {
        weather = newWeather;
    }

    ViewGroup getWeatherLayout() {
        return weatherLayout;
    }

    View getTabView() {
        return view;
    }

    void setTabView(View newView) {
        view = newView;
    }

    TextView getDistanceView() {
        return distanceTextView;
    }

    TextView getStepView() {
        return stepTextView;
    }

    TextView getTvPollen() {
        return tvPollen;
    }

    TextView getTempView(){return tempView;}

    //
    // NOT IN USE , A MORE MEMORY SAVING METHOD IS USED
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
    public TreeView getAnimTree(){
        return treeAnimView;
    }
}