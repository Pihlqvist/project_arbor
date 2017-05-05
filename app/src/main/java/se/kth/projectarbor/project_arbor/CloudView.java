package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.ViewUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by fredrik on 2017-05-04.
 */

public class CloudView {

    private static final String TAG = "ARBOR_CLOUDVIEW";

    private ImageView[] clouds;
    private ConstraintLayout.LayoutParams layoutParams;
    private Float randomFloat;
    private int randomInt1;
    private int randomInt2;
    private Random random;

    private Float SCALE_MIN = (float) 0.45;
    private Float SCALE_MAX = (float) 0.55;

    private int POSITION_TOP = 1;
    private int POSITION_LEFT = 1;
    private int POSITION_RIGHT = 1;
    private int POSITION_BOTTOM = 1;

    private Animation animCloud1;
    private Animation animCloud2;
    private Animation animCloud3;

    private TranslateAnimation animCloud;


    public CloudView(Context context) {

        random = new Random();
        clouds = new ImageView[3];

        Log.d(TAG, "CloudView construct");

        animCloud = (TranslateAnimation) AnimationUtils.loadAnimation(context, R.anim.move_right);

        animCloud1 = AnimationUtils.loadAnimation(context, R.anim.move_right_1);
        animCloud2 = AnimationUtils.loadAnimation(context, R.anim.move_right_2);
        animCloud3 = AnimationUtils.loadAnimation(context, R.anim.move_right_3);

        layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i=0; i<clouds.length; i++) {
            clouds[i] = new ImageView(context);
            clouds[i].setImageResource(R.drawable.cloud_1);

            randomFloat = getRandFloat(SCALE_MIN, SCALE_MAX);
            clouds[i].setScaleX(randomFloat);
            clouds[i].setScaleY(randomFloat);


            clouds[i].setLayoutParams(layoutParams);
        }

        clouds[0].startAnimation(animCloud1);
        clouds[1].startAnimation(animCloud2);
        clouds[2].startAnimation(animCloud3);




    }


    public ConstraintLayout addViews(ConstraintLayout layout) {
        for (ImageView imageView : clouds) {
            layout.addView(imageView);
            Log.d(TAG, "addView");
        }
        return layout;
    }

    private float getRandFloat(float min, float max) {
        return random.nextFloat() * (max - min) + min;
    }

    private int getRandInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }


}
