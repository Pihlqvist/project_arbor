package se.kth.projectarbor.project_arbor.view_objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.animation.Animation;;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.Random;
import se.kth.projectarbor.project_arbor.R;
import static android.view.animation.AnimationUtils.currentAnimationTimeMillis;

/**
 * Created by Fredrik Pihlqvist and Lazar Cerovic on 2017-05-04.
 *
 * Clouds travel from left to right
 * Add the images by giving a layout and the same layout with the
 * images is returned.
 */

public class CloudView {

    // Variables for cloud animation
    private final static float CLOUD_SMALL = 0.4f;
    private final static float CLOUD_BIG = 0.6f;
    private final static float CLOUD_ALPHA = 0.9f;
    private final static float CLOUD_Y_START = 0.0f;
    private final static float CLOUD_Y_END = 0.12f;
    private final static int CLOUD_HIGH_DURATION = 34000;
    private final static int CLOUD_LOW_DURATION = 30000;
    private final static int CLOUD_OFFSET = 3500;
    private final static int CLOUD_AMOUNT = 9;

    // Saves the clouds and animation in this array
    private ImageView[] clouds;

    private Random random;


    public CloudView(Context context) {

        random = new Random();
        clouds = new ImageView[CLOUD_AMOUNT];
        RelativeLayout.LayoutParams layoutParams;
        int screenHeight;
        int screenWidth;

        // Saves the image id for use later
        int[] cloudResources = new int[3];
        cloudResources[0] = R.drawable.dark_cloud_1;
        cloudResources[1] = R.drawable.dark_cloud_2;
        cloudResources[2] = R.drawable.dark_cloud_3;

        // Get Width and Height of the available screen size of the context
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        // Get resolution information about the cloud.png
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.dark_cloud_1, opt);


        for (int i=0; i<clouds.length; i++) {

            // Make png to ImageView
            ImageView ImageViewCloud = new ImageView(context);
            ImageViewCloud.setImageResource(cloudResources[i%3]);

            // Set the transparency of the cloud to 90 %
            ImageViewCloud.setAlpha(CLOUD_ALPHA);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // This changes the size cloud with the original ratios. The width will be
            // specific % of the screens width defined above
            layoutParams.width = (int) (screenWidth * getRandFloat(CLOUD_SMALL, CLOUD_BIG));
            layoutParams.height = (layoutParams.width * opt.outHeight) / opt.outWidth;
            ImageViewCloud.setLayoutParams(layoutParams);

            // Animates the cloud. Starts of screen and ends of screen. Y-position is random.
            // Speed is random,
            Float heightSpawn = getRandFloat(CLOUD_Y_START, CLOUD_Y_END);
            TranslateAnimation cloudAnimation = new TranslateAnimation(
                    (-1f * layoutParams.width)
                    , (1f * screenWidth)
                    , heightSpawn * screenHeight, heightSpawn * screenHeight);
            cloudAnimation.setDuration(getRandInt(CLOUD_LOW_DURATION, CLOUD_HIGH_DURATION));
            cloudAnimation.setRepeatMode(Animation.RESTART);
            cloudAnimation.setRepeatCount(Animation.INFINITE);
            cloudAnimation.setStartTime(currentAnimationTimeMillis() + i*CLOUD_OFFSET);

            ImageViewCloud.setAnimation(cloudAnimation);

            clouds[i] = ImageViewCloud;
        }

    }

    // Send the current layout and this will add all the clouds to it and return it.
    public RelativeLayout addViews(RelativeLayout layout) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
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
