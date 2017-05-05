package se.kth.projectarbor.project_arbor;
/**
 * Created by Joseph on 4/3/2017.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class ProgressBarTest extends AppCompatActivity {

//

    private EditText etPercent;
    private ClipDrawable waterAnim;
    private ClipDrawable sunAnim;


    private int mLevel = 0;
    private int fromLevel = 0;
    private int toLevel = 0;

    public static final int MAX_LEVEL = 10000;
    public static final int LEVEL_DIFF = 100;
    public static final int DELAY = 30;

    private Handler mRightHandler = new Handler();
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

    ImageView imgWater;
    ImageView imgSun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar_test);

        etPercent = (EditText) findViewById(R.id.etPercent);

        imgWater = (ImageView) findViewById(R.id.ivXmlWater);  //XMl file in drawable clip_source1
        imgSun = (ImageView)findViewById(R.id.ivXmlSun);  // Xml file in drawable clip_source2

        waterAnim = (ClipDrawable) imgWater.getDrawable();
        waterAnim.setLevel(0);
        sunAnim = (ClipDrawable) imgSun.getDrawable();
        sunAnim.setLevel(0);

    }

    private void fillBuffer(int fromLevel, int toLevel) {
        mLevel += LEVEL_DIFF;
        waterAnim.setLevel(mLevel);
        sunAnim.setLevel(mLevel);
        if (mLevel <= toLevel) {
            mRightHandler.postDelayed(animateUpImage, DELAY);
        } else {
            mRightHandler.removeCallbacks(animateUpImage);
            ProgressBarTest.this.fromLevel = toLevel;
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
            ProgressBarTest.this.fromLevel = toLevel;
        }
    }

    // Filling and unfilling the buffers depending on textview value
    public void onClickOk(View v) {
        int temp_level = ((Integer.parseInt(etPercent.getText().toString())) * MAX_LEVEL) / 100;

        if (toLevel == temp_level || temp_level > MAX_LEVEL) {
            return;
        }
        toLevel = (temp_level <= MAX_LEVEL) ? temp_level : toLevel;
        if (toLevel > fromLevel) {
            // cancel previous process first
            mLeftHandler.removeCallbacks(animateDownImage);
            ProgressBarTest.this.fromLevel = toLevel;

            mRightHandler.post(animateUpImage);
        } else {
            // cancel previous process first
            mRightHandler.removeCallbacks(animateUpImage);
            ProgressBarTest.this.fromLevel = toLevel;

            mLeftHandler.post(animateDownImage);
        }
    }
}