package se.kth.projectarbor.project_arbor;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by fredrik on 2017-05-05.
 */

public class RainView {

    private Random random = new Random();

    private ImageView darkCloudIV;
    private ImageView[] raindropsIV;

    private final int NUM_OF_DROPS = 8;
    private final int SCREEN_WIDTH_DIVISOR = 3;
    private final float SCREEN_X_OFFSET_PERCENTAGE = 0.75f;
    private final float SCREEN_Y_OFFSET_PERCENTAGE = 0.10f;

    private RelativeLayout.LayoutParams layoutParams;

    public RainView(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), R.drawable.rain_cloud_1, opt);

        raindropsIV = new ImageView[NUM_OF_DROPS];
        darkCloudIV = new ImageView(activity);
        darkCloudIV.setImageResource(R.drawable.rain_cloud_1);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.width = size.x/SCREEN_WIDTH_DIVISOR;
        layoutParams.height = layoutParams.width * opt.outHeight / opt.outWidth;
        layoutParams.leftMargin = (int) (SCREEN_X_OFFSET_PERCENTAGE * size.x - layoutParams.width/2);
        layoutParams.topMargin = (int) (SCREEN_Y_OFFSET_PERCENTAGE * size.y - layoutParams.height/2);
        darkCloudIV.setLayoutParams(layoutParams);

        for (int i = 0; i < NUM_OF_DROPS; i++) {
            ImageView drop = new ImageView(activity);
            drop.setImageResource(R.drawable.rain_drop);
            raindropsIV[i] = drop;

            Animation animation = getDropAnimation((int) (SCREEN_X_OFFSET_PERCENTAGE * size.x) + random.nextInt(layoutParams.width) - layoutParams.width/2,
                    (int) (SCREEN_Y_OFFSET_PERCENTAGE * size.y), size.y);
            drop.startAnimation(animation);
        }
    }

    private Animation getDropAnimation(int x, int fromY, int toY) {
        final int DURATION_LOWER_BOUND = 1700;
        final int DURATION_HIGH_BOUND = 2000;
        int rand = random.nextInt(DURATION_HIGH_BOUND - DURATION_LOWER_BOUND) + DURATION_LOWER_BOUND;
        TranslateAnimation translate =
                new TranslateAnimation(x, x, fromY, toY);
        translate.setDuration(rand);
        translate.setRepeatMode(Animation.RESTART);
        translate.setRepeatCount(Animation.INFINITE);

        return translate;
    }

    public ViewGroup addViews(ViewGroup layout) {
        for (ImageView view : raindropsIV) {
            layout.addView(view);
        }

        layout.addView(darkCloudIV);

        return layout;
    }
}
