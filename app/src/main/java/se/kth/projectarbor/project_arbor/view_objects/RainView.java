package se.kth.projectarbor.project_arbor.view_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.Random;

import se.kth.projectarbor.project_arbor.R;

/**
 * Created by fredrik on 2017-05-05.
 */

public class RainView {

    private Random random = new Random();
    private Animation anim;
    private ImageView[] darkClouds;
    private int[] darkCloudResources;
    private ImageView[] raindropsIV;

    private final int NUM_OF_CLOUDS = 3;
    private final int NUM_OF_DROPS = 6;
    private final float SCREEN_WIDTH_DIVISOR = 2f;
    private final float SCREEN_X_OFFSET_PERCENTAGE = 0.22f;
    private final float SCREEN_Y_OFFSET_PERCENTAGE = 0.13f;
    private final float DROPS_ANIMATION_END_PERCENTAGE = 0.65f;

    private RelativeLayout.LayoutParams layoutParams;

    public RainView(Activity activity) {
        darkClouds = new ImageView[NUM_OF_CLOUDS];
        darkCloudResources = new int[3];
        darkCloudResources[0] = R.drawable.rain_cloud_1;
        darkCloudResources[1] = R.drawable.rain_cloud_2;
        darkCloudResources[2] = R.drawable.rain_cloud_3;

        int heights[] = {1,2,1};
        int leftMargins[] = new int[3];
        int cloudWidths[] = new int[3];

        // Get available display information
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Get width and height of raincloud
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), R.drawable.rain_cloud_1, opt);

        for(int i = 0; i < NUM_OF_CLOUDS; i++){
            ImageView darkCloudIV = new ImageView(activity);
            darkCloudIV.setImageResource(darkCloudResources[i]);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // Resize the raincloud depending on screen size
            layoutParams.width = (int)((float)(size.x)/SCREEN_WIDTH_DIVISOR);
            layoutParams.height = layoutParams.width * opt.outHeight / opt.outWidth;
            cloudWidths[i] = layoutParams.width;

            // Where to place the raincloud in the view
            layoutParams.leftMargin = (int) (SCREEN_X_OFFSET_PERCENTAGE * size.x - layoutParams.width/4 + layoutParams.width/3 * i);
            layoutParams.topMargin = (int) (SCREEN_Y_OFFSET_PERCENTAGE * size.y - layoutParams.height/2 * heights[i]);
            leftMargins[i] = layoutParams.leftMargin;

            anim = AnimationUtils.loadAnimation(darkCloudIV.getContext(),R.anim.dark_cloud_anim);
            darkCloudIV.startAnimation(anim);
            darkCloudIV.setLayoutParams(layoutParams);
            darkClouds[i] = darkCloudIV;
        }
        // Assign raincloud and raindrops
        raindropsIV = new ImageView[NUM_OF_DROPS/* *2 */];


        for (int i = 0; i < NUM_OF_DROPS; i++) {
            ImageView drop = new ImageView(activity);
            drop.setImageResource(R.drawable.rain_drop);
            raindropsIV[i] = drop;

            // Placement and animation start and end of the drops
            int rainDropsWidth = cloudWidths[0] + (cloudWidths[2]/2);
            Animation animation = getDropAnimation(
                    (leftMargins[0] + (rainDropsWidth/(NUM_OF_DROPS+1)) * (i+1))
                    , layoutParams.topMargin + layoutParams.height/2 + random.nextInt(layoutParams.height)/2
                    , (int)(size.y * DROPS_ANIMATION_END_PERCENTAGE));
            drop.startAnimation(animation);
        }
    }

    // Gives a random animation to the raindrops
    private Animation getDropAnimation(int x, int fromY, int toY) {
        // Determine the speed of the drops
        final int DURATION_LOWER_BOUND = 1700;
        final int DURATION_HIGH_BOUND = 2000;
        int rand = random.nextInt(DURATION_HIGH_BOUND - DURATION_LOWER_BOUND) + DURATION_LOWER_BOUND;
        TranslateAnimation translate =
                new TranslateAnimation(x, x, fromY, toY);
        translate.setDuration(rand);
        translate.setRepeatMode(Animation.RESTART);
        translate.setRepeatCount(Animation.INFINITE);

        // Makes a fade out on the drops
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
        alphaAnimation.setRepeatMode(Animation.RESTART);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setStartTime(rand/2);
        alphaAnimation.setDuration(rand);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translate);
        animationSet.addAnimation(alphaAnimation);

        return animationSet;
    }

    public ViewGroup addViews(ViewGroup layout) {
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(layoutParams);
        for (ImageView view : raindropsIV) {
            layout.addView(view);
        }
        for(ImageView rainCloud: darkClouds){
            layout.addView(rainCloud);
        }

        return layout;
    }

}
