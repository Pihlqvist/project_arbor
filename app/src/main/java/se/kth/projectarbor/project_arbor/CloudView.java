package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import static android.view.animation.AnimationUtils.currentAnimationTimeMillis;

/**
 * Created by fredrik on 2017-05-04.
 *
 * Creates clouds and animates them
 */

public class CloudView {

    private static final String TAG = "ARBOR_CLOUDVIEW";

    private ImageView[] clouds;
    private int[] cloudResources;
    private RelativeLayout.LayoutParams layoutParams;
    private Random random;

    private int screenHeight;
    private int screenWidth;

    private static int CLOUD_AMOUNT = 4;


    public CloudView(Context context) {

        random = new Random();
        clouds = new ImageView[CLOUD_AMOUNT];
        cloudResources = new int[4];
        cloudResources[0] = R.drawable.cloud_1;
        cloudResources[1] = R.drawable.cloud_2;
        cloudResources[2] = R.drawable.cloud_3;
        cloudResources[3] = R.drawable.cloud_4;

        // Get Width and Height of the available screen size of the context
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        Log.d(TAG, "Screen Width: " + screenWidth + ", Height: " + screenHeight);

        // Get information about the cloud.png
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.cloud_1, opt);


        for (int i=0; i<clouds.length; i++) {

            // Make png to ImageView
            ImageView cloudIV = new ImageView(context);
            cloudIV.setImageResource(cloudResources[i]);

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
                    (-1f * layoutParams.width)
                    , (1f * screenWidth)
                    , heightSpawn * screenHeight, heightSpawn * screenHeight);
            cloudAnimation.setDuration(getRandInt(30000, 33000));
            cloudAnimation.setRepeatMode(Animation.RESTART);
            cloudAnimation.setRepeatCount(Animation.INFINITE);
            cloudAnimation.setStartTime(currentAnimationTimeMillis() + i*5000);

            cloudIV.setAnimation(cloudAnimation);

            clouds[i] = cloudIV;
        }

    }

    // Send the current layout and this will add all the clouds to it and return it.
    public RelativeLayout addViews(RelativeLayout layout) {
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(layoutParams);
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
