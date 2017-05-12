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

import org.w3c.dom.Text;

import java.text.DecimalFormat;


/**
 * Created by Fredrik Pihlqvist on 2017-04-28.
 * Edited by Jospeh Ariss and Pethrus Gardborn on 4/5/2017
 * Edited by Lazar Eriksson and Patrik Cerovic on 12/5/2017
 */

public class StatsTab extends Fragment {

    // Variables and constants used for static change of buffers (no animation)

    private final static String TAG = "ARBOR_STATSTAB";

    private double mDistance;
    private int mStepCount;
    private int currentPhaseNumber;

    private View view;

    private TextView healthTV;
    private TextView totalStepsTV;
    private TextView phaseTV;
    private TextView totalDistanceTV;
    private TextView ageTV; // TODO: Implement ageTV

    private ClipDrawable waterAnim;
    private ClipDrawable sunAnim;
    public static final int MAX_LEVEL = 10000;
    ImageView imgWater;
    ImageView imgSun;
    ImageView phaseIcon;

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


/*  // TODO: See if MainUI does this correctly
    private class ReceiverStats extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            DecimalFormat twoDForm = new DecimalFormat("#.0");
            Log.d(TAG, "onReceive()");

            // Msgs from MainService:tree data

            if (intent.getAction().equals(MainService.TREE_DATA)) {
                if (extras.getInt("HP") < 1) {
                    healthTV.setText("DEAD");
                } else {
                    healthTV.setText("" + extras.getInt("HP") + "hp");
                }

                // LAZAR OCH PATRIK ANVÄNDER DESSA
                mDistance = extras.getDouble("TOTALKM")/1000;  // 1000 is BUFFER_CONSTANT in Pedometer
                mStepCount = extras.getInt("TOTALSTEPS");
                currentPhaseNumber = ((Tree.Phase) extras.get("PHASE")).getPhaseNumber();
                statusImgUpd(phaseIcon);
                // SLUT PÅ DERAS

                phaseTV.setText(((Tree.Phase) extras.get("PHASE")).getPhaseName());

                // TODO: Implement AGE when functionality is ready
                waterAnim.setLevel(extras.getInt("WATER") * 10);
                sunAnim.setLevel(extras.getInt("SUN") * 10);
                totalDistanceTV.setText(String.format("%.2f", (extras.getDouble("TOTALKM")/1000)));
                totalStepsTV.setText(String.format("%d", (extras.getInt("TOTALSTEPS"))));

            } else if (intent.getAction().equals(Pedometer.DISTANCE_BROADCAST)) {
                totalDistanceTV.setText(String.format("%.2f", (extras.getDouble("TOTALDISTANCE")/1000)));
                totalStepsTV.setText(String.format("%d", (extras.getInt("TOTALSTEPCOUNT"))));
            }
        }
    }
*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_stats_tab, container, false);

        Log.d(TAG, "onCreateView in tree tab");

        setupValues();

        /* // TODO: See if it works after intergirsh
        // Setup a filter for views
        IntentFilter filter = new IntentFilter();
        filter.addAction(Pedometer.DISTANCE_BROADCAST);
        filter.addAction(MainService.TREE_DATA);
        getActivity().registerReceiver(this.new ReceiverStats(), filter);
        */
        // TEST to set levels manually

//          waterAnim.setLevel(5000);
//        sunAnim.setLevel(7500);
//        healthTV.setText("HP: LIVING");
//        totalStepsTV.setText("Many totalStepsTV");
//        phaseTV.setText("SEED");
//        ageTV.setText("Age");
//        totalDistanceTV.setText("");

        Intent intent = getActivity().getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            Log.d(TAG, "Setup from Intent");
            healthTV.setText("" + extras.getInt("HP") + " HP");
            totalStepsTV.setText(String.format("%d steps", extras.getInt("TOTALSTEPS")));
            totalDistanceTV.setText(String.format("%.2f", (extras.getDouble("TOTALKM")/1000)));
            phaseTV.setText(((Tree.Phase) extras.get("PHASE")).getPhaseName());
            waterAnim.setLevel(extras.getInt("WATER") * 10);
            sunAnim.setLevel(extras.getInt("SUN") * 10);
           // totalDistanceTV.setText( extras.getInt("DISTANCE"));
        }


        return view;
    }

    // Setup all the views
    private void setupValues() {
        healthTV = (TextView) view.findViewById(R.id.tvHealth);
        ageTV = (TextView) view.findViewById(R.id.tvAge);
        phaseTV = (TextView) view.findViewById(R.id.tvPhase);
        totalStepsTV = (TextView) view.findViewById(R.id.tvSteps);
        totalDistanceTV = (TextView) view.findViewById(R.id.tvDistance);

        imgWater = (ImageView) view.findViewById(R.id.ivXmlWater);  //XMl file in drawable clip_source1
        imgSun = (ImageView) view.findViewById(R.id.ivXmlSun);  // Xml file in drawable clip_source2
        phaseIcon = (ImageView) view.findViewById(R.id.ivPhaseIcon);
        waterAnim = (ClipDrawable) imgWater.getDrawable();
        waterAnim.setLevel(0);
        sunAnim = (ClipDrawable) imgSun.getDrawable();
        sunAnim.setLevel(0);
    }
    private void statusImgUpd(ImageView v){
        v.setImageResource(getPhaseImage());
    }
    // Can be changed for testing purposes, change mDistance to meter instead of kilometers
    // Do not change here change the variable instead.
    //This method divides the amount of distance between 0 - 60 (km) in intervals and changes the image (phase icon)
    private int getPhaseImage() {

        if(mDistance <= 60){
            // TODO: Make a tree varibles class that we can ask
            Tree tree = new Tree();
            double k = (mDistance - tree.getNextPhase(currentPhaseNumber-1)) /
                    tree.getNextPhase(currentPhaseNumber) - tree.getNextPhase(currentPhaseNumber-1);
            tree = null;
            if(Double.compare(k, 0.125) < 0){
                return R.drawable.phase_icon;
            } else if (Double.compare(k, 0.25) < 0){
                return R.drawable.phase_1;
            } else if (Double.compare(k, 0.375) < 0){
                return R.drawable.phase_2;
            } else if (Double.compare(k, 0.5) < 0){
                return R.drawable.phase_3;
            } else if (Double.compare(k, 0.625) < 0){
                return R.drawable.phase_4;
            } else if (Double.compare(k, 0.75) < 0){
                return R.drawable.phase_5;
            } else if (Double.compare(k, 0.875) < 0){
                return R.drawable.phase_6;
            } else if (Double.compare(k, 1) < 0){
                return R.drawable.phase_7;
            } else if (Double.compare(k, 1) == 0){
                return R.drawable.phase_8;
            }

        }
        return R.drawable.phase_icon;
    }

    // TODO: Change to the right names
    TextView getDistanceView() {
        return totalDistanceTV;
    }

    TextView getStepsView() {
        return totalStepsTV;
    }

    TextView getHealthView() {
        return healthTV;
    }

    TextView getPhaseView() {
        return phaseTV;
    }

    ClipDrawable getWaterAnim() {
        return waterAnim;
    }

    ClipDrawable getSunAnim() {
        return sunAnim;
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
