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



    private EditText etPercent;
    private ClipDrawable mImageDrawable;
    private ClipDrawable mImageDrawable2;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar_test);

        etPercent = (EditText) findViewById(R.id.etPercent);


        ImageView img = (ImageView) findViewById(R.id.imageView1);  //XMl file in drawable clip_source1
        ImageView img2 = (ImageView)findViewById(R.id.imageView3);  // Xml file in drawable clip_source2


        mImageDrawable = (ClipDrawable) img.getDrawable();
        mImageDrawable.setLevel(0);
        mImageDrawable2= (ClipDrawable) img2.getDrawable();
        mImageDrawable2.setLevel(0);

        FrameLayout fram1 = (FrameLayout) findViewById(R.id.Frame1);
        FrameLayout fram2 = (FrameLayout) findViewById(R.id.Frame2);

    }

    private void fillBuffer(int fromLevel, int toLevel) {
        mLevel += LEVEL_DIFF;
        mImageDrawable.setLevel(mLevel);
        mImageDrawable2.setLevel(mLevel);
        if (mLevel <= toLevel) {
            mRightHandler.postDelayed(animateUpImage, DELAY);
        } else {
            mRightHandler.removeCallbacks(animateUpImage);
            ProgressBarTest.this.fromLevel = toLevel;
        }
    }

    private void unfillBuffer(int fromLevel, int toLevel) {
        mLevel -= LEVEL_DIFF;
        mImageDrawable.setLevel(mLevel);
        mImageDrawable2.setLevel(mLevel);
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