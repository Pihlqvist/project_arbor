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

    private int oldWaterLevel;
    private int oldSunLevel;
    private int newWaterLevel;
    private int newSunLevel;

    private SharedPreferences sharedPreferences;

    public static final int LEVEL_DIFF = 100;  // Difference btw current level and level we want to reach.
    public static final int DELAY = 50;
    private int mLevel;
    private int fromLevelWater;
    private int toLevelWater;
    private int fromLevelSun;
    private int toLevelSun;

    private Handler mRightHandler = new Handler();
    private Runnable animateUpImageWater = new Runnable() {

        @Override
        public void run() {
            fillBuffer(fromLevelWater, toLevelWater, waterAnim);
        }
    };
    private Runnable animateUpImageSun = new Runnable() {

        @Override
        public void run() {
            fillBuffer(fromLevelSun, toLevelSun, sunAnim);
        }
    };

    // Left handler uses unfillBuffer
    private Handler mLeftHandler = new Handler();

    private Runnable animateDownImageWater = new Runnable() {

        @Override
        public void run() {
            unfillBuffer(fromLevelWater, toLevelWater, waterAnim);
        }
    };

    private Runnable animateDownImageSun = new Runnable() {

        @Override
        public void run() {
            unfillBuffer(fromLevelSun, toLevelSun, sunAnim);
        }
    };

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
            Log.d("ARBOR_STATSTAB", "StatsTab->extras!=null inside");
            health.setText("" + extras.getInt("HP") + "hp");
            steps.setText("" + extras.getInt("STEPCOUNT") + "steps");
            phase.setText(extras.getString("PHASE"));
            waterAnim.setLevel(extras.getInt("WATER") * 10);
            sunAnim.setLevel(extras.getInt("SUN") * 10);

            oldWaterLevel = newWaterLevel = extras.getInt("WATER") * 10;
            oldSunLevel = newSunLevel = extras.getInt("SUN") * 10;

            dist.setText("" + extras.getDouble("DISTANCE"));

            // Sets old value for animation the very first time the activity is started
            if (!sharedPreferences.contains("BARS_WATERLEVEL")) {
                setOldWaterLevel(extras.getInt("WATER") * 10);
                Log.d("ARBOR_STATSTAB_ANIM", "StatsTab.onCreateView() First: " + extras.getInt("WATER") * 10 + "");
            }
            if (!sharedPreferences.contains("BARS_SUNLEVEL")) {
                setOldSunLevel(extras.getInt("SUN")  * 10);
                Log.d("ARBOR_STATSTAB_ANIM", "StatsTab.onCreateView() First: " + extras.getInt("SUN") * 10 + "");
            }

            waterAnim.setLevel(oldWaterLevel);
            sunAnim.setLevel(oldSunLevel);
        }


        return view;
    }

    // Setup all the views
    private void setupValues() {
        sharedPreferences = getActivity().getSharedPreferences("se.kth.projectarbor.project_arbor", Context.MODE_PRIVATE);

        health = (TextView) view.findViewById(R.id.tvHealth);
        age = (TextView) view.findViewById(R.id.tvAge);
        phase = (TextView) view.findViewById(R.id.tvPhase);
        steps = (TextView) view.findViewById(R.id.tvSteps);
        dist = (TextView) view.findViewById(R.id.tvDistance);

        imgWater = (ImageView) view.findViewById(R.id.ivXmlWater);  //XMl file in drawable clip_source1
        imgSun = (ImageView) view.findViewById(R.id.ivXmlSun);  // Xml file in drawable clip_source2

        waterAnim = (ClipDrawable) imgWater.getDrawable();
        sunAnim = (ClipDrawable) imgSun.getDrawable();
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
        sharedPreferences.edit().putInt("BARS_SUNLEVEL", oldLevel).apply();
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

    // LAST METHODS USED ONLY WHEN ANIMATION IS IMPLEMENTED
    private void fillBuffer(int fromLevel, int toLevel, ClipDrawable clipDrawable) {
        mLevel += LEVEL_DIFF;
        clipDrawable.setLevel(mLevel);

        Runnable animateUpImage = null;
        if (clipDrawable == waterAnim) {
            animateUpImage = animateUpImageWater;

            if (mLevel <= toLevel) {
                mRightHandler.postDelayed(animateUpImage, DELAY);
            } else {
                mRightHandler.removeCallbacks(animateUpImageWater, animateUpImageSun);
                this.fromLevelWater = newWaterLevel;
            }
        } else {
            animateUpImage = animateUpImageSun;

            if (mLevel <= toLevel) {
                mRightHandler.postDelayed(animateUpImage, DELAY);
            } else {
                mRightHandler.removeCallbacks(animateUpImageWater, animateUpImageSun);
                this.fromLevelSun = toLevel;
            }
        }
    }

    private void unfillBuffer(int fromLevel, int toLevel, ClipDrawable clipDrawable) {
        mLevel -= LEVEL_DIFF;
        clipDrawable.setLevel(mLevel);

        Runnable animateDownImage = null;
        if (clipDrawable == waterAnim) {
            animateDownImage = animateDownImageWater;
            if (mLevel >= toLevel) {
                mLeftHandler.postDelayed(animateDownImage, DELAY);
            } else {
                mLeftHandler.removeCallbacks(animateDownImageWater, animateDownImageSun);
                this.fromLevelWater = newWaterLevel;
            }
        } else {
            animateDownImage = animateDownImageSun;
            if (mLevel >= toLevel) {
                mLeftHandler.postDelayed(animateDownImage, DELAY);
            } else {
                mLeftHandler.removeCallbacks(animateDownImageWater, animateDownImageSun);
                this.fromLevelSun = toLevel;
            }
        }
    }

    // Filling and unfilling the buffers depending on textview value
    public void changeBuffer(int percentage, ClipDrawable clipDrawable) {
        int temp_level = percentage; // (percentage * MAX_LEVEL) / 100;

            /*if (clipDrawable == waterAnim) {
                toLevel = newWaterLevel;
                fromLevel = oldWaterLevel;
            } else {
                toLevel = newSunLevel;
                fromLevel = oldSunLevel;
            }*/

        if (clipDrawable == waterAnim) {
            if (toLevelWater == temp_level || temp_level > MAX_LEVEL) {
                return;
            }

            toLevelWater = (temp_level <= MAX_LEVEL) ? temp_level : toLevelWater;
            if (toLevelWater > fromLevelWater) {
                // cancel previous process first
                mLeftHandler.removeCallbacks(animateDownImageWater, animateDownImageSun);
                this.fromLevelWater = newWaterLevel;

                mRightHandler.post(animateUpImageWater);
            } else {
                // cancel previous process first
                mRightHandler.removeCallbacks(animateUpImageWater, animateUpImageSun);
                this.fromLevelWater = newWaterLevel;

                mLeftHandler.post(animateDownImageWater);
            }

        } else {
            if (toLevelSun == temp_level || temp_level > MAX_LEVEL) {
                return;
            }

            toLevelSun = (temp_level <= MAX_LEVEL) ? temp_level : toLevelSun;
            if (toLevelSun > fromLevelSun) {
                // cancel previous process first
                mLeftHandler.removeCallbacks(animateDownImageWater, animateDownImageSun);
                this.fromLevelSun = toLevelSun;
                mRightHandler.post(animateUpImageSun);
            } else {
                // cancel previous process first
                mRightHandler.removeCallbacks(animateUpImageWater, animateUpImageSun);
                this.fromLevelSun = toLevelSun;
                mLeftHandler.post(animateDownImageSun);
            }

        }
    }

//        public void animateWaterBar() {
//            fromLevel = oldWaterLevel;
//            toLevel = newWaterLevel;
//            if (newWaterLevel > oldWaterLevel) {
//                mLeftHandler.removeCallbacks(animateDownImage);
//                mRightHandler.post(animateUpImage);
//            } else {
//                mLeftHandler.removeCallbacks(animateUpImage);
//                mRightHandler.post(animateDownImage);
//            }
//        }
}
