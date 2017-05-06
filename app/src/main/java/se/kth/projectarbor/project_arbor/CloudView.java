package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.ViewUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by fredrik on 2017-05-04.
 *
 * Creates clouds and animates them
 */

public class CloudView {

    private static final String TAG = "ARBOR_CLOUDVIEW";

    private ImageView[] clouds;
    private RelativeLayout.LayoutParams layoutParams;
    private Random random;

    private int screenHeight;
    private int screenWidth;

    private static int CLOUD_AMOUNT = 4;

    // OLD VAR START
    private Float randomFloat;
    private int randomInt1;
    private int randomInt2;

    private Float SCALE_MIN = (float) 0.45;
    private Float SCALE_MAX = (float) 0.55;

    private Animation animCloud1;
    private Animation animCloud2;
    private Animation animCloud3;
    private TranslateAnimation animCloud;
    // OLD VAR END



    public CloudView(Context context) {

        random = new Random();
        clouds = new ImageView[CLOUD_AMOUNT];

        // Get Width and Height of the available screen size of the context
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        Log.d(TAG, "Screen Width: " + screenWidth + ", Height: " + screenHeight);

        // Get information about the cloud.png
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.rain_cloud_1, opt);


        for (int i=0; i<clouds.length; i++) {

            ImageView cloudIV = new ImageView(context);
            cloudIV.setImageResource(R.drawable.cloud_1);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // This changes the size cloud with the original ratios. The width will be
            // 30-40 % of the screens width
            layoutParams.width = (int) (screenWidth * getRandFloat(0.3f, 0.4f));
            layoutParams.height = (layoutParams.width * opt.outHeight) / opt.outWidth;
            Log.d(TAG, "[Cloud "+ i + "] Width: " + layoutParams.width + ", Height: " + layoutParams.height);

            cloudIV.setLayoutParams(layoutParams);

            // Animates the cloud. Starts of screen and ends of screen. Y-position is random.
            // Speed is random,
            Float heightSpawn = getRandFloat(0f, 0.1f);
            TranslateAnimation cloudAnimation = new TranslateAnimation(
                    ((-1f - getRandFloat(i, i*2)) * layoutParams.width), (1f * screenWidth)
                    , heightSpawn * screenHeight, heightSpawn * screenHeight);
            cloudAnimation.setDuration(getRandInt(30000, 40000));
            cloudAnimation.setRepeatMode(Animation.RESTART);
            cloudAnimation.setRepeatCount(Animation.INFINITE);

            cloudIV.setAnimation(cloudAnimation);

            clouds[i] = cloudIV;
        }

        // OLD CODE START
//        clouds[0].startAnimation(animCloud1);
//        clouds[1].startAnimation(animCloud2);
//        clouds[2].startAnimation(animCloud3);
        // OLD CODE END



    }


    // Send the current layout and this will add all the clouds to it and return it.
    public RelativeLayout addViews(RelativeLayout layout) {
        for (ImageView cloud : clouds) {
            layout.addView(cloud);
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
