package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;


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

    // To store old state of bars
    private SharedPreferences sharedPreferences;

    // VARIABLES AND CONSTANTS USED ONLY WHEN ANIMATION IS IMPLEMENTED

    private int oldWaterLevel;
    private int oldSunLevel;
    private int newWaterLevel;
    private int newSunLevel;

        // Right handler uses fillBuffer
        private Handler mRightHandler = new Handler();
        private Runnable animateUpImage = new Runnable() {

            @Override
            public void run() {
                fillBuffer(toLevel, fromLevel);
            }
        };

        // Left handler uses unfillBuffer
        private Handler mLeftHandler = new Handler();
        private Runnable animateDownImage = new Runnable() {

            @Override
            public void run() {
                unfillBuffer(toLevel, fromLevel);
            }
        };
        public static final int LEVEL_DIFF = 100;  // Difference btw current level and level we want to reach.
        public static final int DELAY = 50;
        private int mLevel;
        private int fromLevel;
        private int toLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.view = inflater.inflate(R.layout.fragment_stats_tab, container, false);

        Log.d(TAG, "onCreateView in tree tab");

        setupValues();

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

        // TODO: Implement dynamic animations of bars instead of static changes
        waterAnim = (ClipDrawable) imgWater.getDrawable();
        waterAnim.setLevel(0);
        sunAnim = (ClipDrawable) imgSun.getDrawable();
        sunAnim.setLevel(0);
        // Used to remember state of buffers when last viewed.
        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

    }

    TextView getDistanceView() {
        return dist;
    }

    TextView getStepsView() {
        return steps;
    }

    TextView getHealthView() {
        return health;
    }

    TextView getPhaseView() {
        return phase;
    }

    ClipDrawable getWaterAnim() {
        return waterAnim;
    }

    ClipDrawable getSunAnim() {
        return sunAnim;
    }

    public void setNewWaterLevel(int newLevel) {
        this.newWaterLevel = newLevel;
    }

    public void setNewSunLevel(int newLevel) {
        this.newSunLevel = newLevel;
    }

    public void setOldWaterLevel(int oldLevel) {
        this.oldWaterLevel = oldLevel;
        sharedPreferences.edit().putInt("BARS_WATERLEVEL", oldLevel).apply();
    }

    public void setOldSunLevel(int oldLevel) {
        this.oldSunLevel = oldLevel;
    }

    public int getNewWaterLevel() {
        return this.newWaterLevel;
    }

    public int getNewSunLevel() {
        return this.newSunLevel;
    }

    public int getOldWaterLevel() {
        return this.oldWaterLevel;
    }

    public int getOldSunLevel() {
        return this.oldSunLevel;
    }

    private int oldWaterLevel() {
        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

        // If level has been stored earlier, read from sharedPreferences
        if (sharedPreferences.contains("BARS_WATERLEVEL")) {
            oldWaterLevel = sharedPreferences.getInt("BARS_WATERLEVEL", 0);
            // Else, set initial water level
        } else {
            oldWaterLevel = 10000;
            sharedPreferences.edit().putInt("BARS_WATERLEVEL", oldWaterLevel).apply();
        }
        return oldWaterLevel;
    }

    // TODO: Complete animation methods
    public void animateWaterBar(int newWaterLevel) {
        oldWaterLevel = oldWaterLevel();
        if(newWaterLevel > oldWaterLevel)
            fillBuffer(oldWaterLevel, newWaterLevel);
        if(newWaterLevel < oldWaterLevel)
            unfillBuffer(oldWaterLevel, newWaterLevel);
        // else do nothing
    }

    public void animateSunBar(int toLevel) {

    }

    // LAST METHODS USED ONLY WHEN ANIMATION IS IMPLEMENTED

    //*

        private void fillBuffer(int fromLevel, int toLevel ) {
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
        public void changeBuffer(int newLevel) {
//            int currentLevel = getOldWaterLevel();
//            fromLevel = getOldWaterLevel();
            Log.d("ANIMATION", "OLDWATERLEVEL " + fromLevel);

            fromLevel = 10000;

            System.out.println("from level " + this.fromLevel);


//            if (toLevel == currentLevel || currentLevel > MAX_LEVEL) {
//                return;
            toLevel = newLevel;
            System.out.println("to level "+ toLevel);
//            this.toLevel = (currentLevel <= MAX_LEVEL) ? currentLevel : this.toLevel;

            // Determines if buffer animation should go up or down.
//            this.toLevel = (fromLevel <= MAX_LEVEL) ? fromLevel : this.toLevel;
            System.out.println("to level2 "+ toLevel);

            if (newLevel > fromLevel) {
                Log.d("ANIMATION", "IF");
                // cancel previous process first
                mLeftHandler.removeCallbacks(animateUpImage);
                this.fromLevel = toLevel;

                mRightHandler.post(animateDownImage);
            } else {
                Log.d("ANIMATION", "ELSE");
                // cancel previous process first
                mRightHandler.removeCallbacks(animateDownImage);
                this.fromLevel = toLevel;

                mLeftHandler.post(animateUpImage);
            }


        }

}
