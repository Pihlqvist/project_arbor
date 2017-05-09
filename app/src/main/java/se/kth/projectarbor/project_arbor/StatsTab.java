package se.kth.projectarbor.project_arbor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;


/**
 * Created by Fredrik Pihlqvist on 2017-04-28.
 * Edited by Jospeh Ariss and Pethrus Gardborn on 4/5/2017
 */

public class StatsTab extends Fragment {

    // Variables and constants used for static change of buffers (no animation)

    private final static String TAG = "ARBOR_STATSTAB";

    private TextView health;
    private TextView steps;
    private TextView phase;
    private TextView dist;
    private View view;
    // TODO: Implement age
    private TextView age;

    private ClipDrawable waterAnim;
    private ClipDrawable sunAnim;
    public static final int MAX_LEVEL = 10000;
    ImageView imgWater;
    ImageView imgSun;

    // VARIABLES AND CONSTANTS USED ONLY WHEN ANIMATION IS IMPLEMENTED

        /*private Handler mRightHandler = new Handler();
        private Runnable animateUpImage = new Runnable() {

            @Override
            public void run() {
                fillBuffer(fromLevel, toLevel);
            }
        };
        private Handler mLeftHandler = new Handler();
        private Runnable animateDownImage = new Runnable() {

            @Override
            public void run() {
                unfillBuffer(fromLevel, toLevel);
            }
        };
        public static final int LEVEL_DIFF = 100;  // Difference btw current level and level we want to reach.
        public static final int DELAY = 30;
        private int mLevel = 0;
        private int fromLevel = 0;
        private int toLevel = 0;*/



    private class ReceiverStats extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            DecimalFormat twoDForm = new DecimalFormat("#.0");
            Log.d("ARBOR_STATAB", "onReceive()");

            // Msgs from Pedometer: steps and distance
            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
               //dist.setText( ""+  (twoDForm.format(     extras.getDouble("TOTALDISTANCE")))); //writes out the total distance with one decimal
            }
            // TODO: Check that receiver message is correct new version of Pedometer is ready
            if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                //steps.setText("" + extras.getInt("TOTALSTEPCOUNT") + " steps");
            }

            // Msgs from MainService:tree data

            if (intent.getAction().equals(MainService.TREE_DATA)) {
                Log.d("HEALTH","HEALTH");
                dist.setText( ""+  extras.getDouble("TOTALKM")/1000);
                steps.setText("" + extras.getInt("TOTALSTEPS") + " steps");
                if (extras.getInt("HP") < 1) {
                    health.setText("DEAD");
                }
                else
                    health.setText("" + extras.getInt("HP") + "hp");
                    phase.setText(extras.getString("PHASE"));

                // TODO: Implement AGE when functionality is ready
                    waterAnim.setLevel(extras.getInt("WATER") * 10);
                    sunAnim.setLevel(extras.getInt("SUN") * 10);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_stats_tab, container, false);

        Log.d(TAG, "onCreateView in tree tab");

        setupValues();

        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new ReceiverStats(), filter);

        // TEST to set levels manually

//          waterAnim.setLevel(5000);
//        sunAnim.setLevel(7500);
//        health.setText("HP: LIVING");
//        steps.setText("Many steps");
//        phase.setText("SEED");
//        age.setText("Age");
//        dist.setText("");

        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            health.setText("" + extras.getInt("HP") + "hp");
            steps.setText("" + extras.getInt("STEPCOUNT") + "steps");
            phase.setText(extras.getString("PHASE"));
            waterAnim.setLevel(extras.getInt("WATER") * 10);
            sunAnim.setLevel(extras.getInt("SUN") * 10);
           // dist.setText( extras.getInt("DISTANCE"));
        }


        return view;
    }

    // Setup all the views
    private void setupValues() {
        health = (TextView) view.findViewById(R.id.tvHealth);
        age = (TextView) view.findViewById(R.id.tvAge);
        phase = (TextView) view.findViewById(R.id.tvPhase);
        steps = (TextView) view.findViewById(R.id.tvSteps);
        dist = (TextView) view.findViewById(R.id.tvDistance);

        imgWater = (ImageView) view.findViewById(R.id.ivXmlWater);  //XMl file in drawable clip_source1
        imgSun = (ImageView) view.findViewById(R.id.ivXmlSun);  // Xml file in drawable clip_source2

        waterAnim = (ClipDrawable) imgWater.getDrawable();
        waterAnim.setLevel(0);
        sunAnim = (ClipDrawable) imgSun.getDrawable();
        sunAnim.setLevel(0);
    }

    // LAST METHODS USED ONLY WHEN ANIMATION IS IMPLEMENTED

    //*

        /*private void fillBuffer(int fromLevel, int toLevel) {
            mLevel += LEVEL_DIFF;
            waterAnim.setLevel(mLevel);
            sunAnim.setLevel(mLevel);
            if (mLevel <= toLevel) {
                mRightHandler.postDelayed(animateUpImage, DELAY);
            } else {
                mRightHandler.removeCallbacks(animateUpImage);
                this.fromLevel = toLevel;
            }
        }

        private void unfillBuffer(int fromLevel, int toLevel) {
            mLevel -= LEVEL_DIFF;
            waterAnim.setLevel(mLevel);
            sunAnim.setLevel(mLevel);
            if (mLevel >= toLevel) {
                mLeftHandler.postDelayed(animateDownImage, DELAY);
            } else {
                mLeftHandler.removeCallbacks(animateDownImage);
                this.fromLevel = toLevel;
            }
        }

        // Filling and unfilling the buffers depending on textview value
        public void changeBuffer(int v) {
            int temp_level = (5 * MAX_LEVEL) / 100;

            if (toLevel == temp_level || temp_level > MAX_LEVEL) {
                return;
            }
            toLevel = (temp_level <= MAX_LEVEL) ? temp_level : toLevel;
            if (toLevel > fromLevel) {
                // cancel previous process first
                mLeftHandler.removeCallbacks(animateDownImage);
                this.fromLevel = toLevel;

                mRightHandler.post(animateUpImage);
            } else {
                // cancel previous process first
                mRightHandler.removeCallbacks(animateUpImage);
                this.fromLevel = toLevel;

                mLeftHandler.post(animateDownImage);
            }
        }*/

}
