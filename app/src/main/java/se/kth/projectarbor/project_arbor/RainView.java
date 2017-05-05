package se.kth.projectarbor.project_arbor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * Created by fredrik on 2017-05-05.
 */

public class RainView {

    private Random random = new Random();
    private int low = 1700;
    private int high = 2000;

    private ImageView darkCloudIV;
    private ImageView drop1;
    private ImageView[] raindropsIV;

    private RelativeLayout.LayoutParams layoutParams;


    public RainView(Activity activity) {
        darkCloudIV = new ImageView(activity);
        darkCloudIV.setImageResource(R.drawable.rain_cloud_1);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), R.drawable.rain_cloud_1, opt);

        layoutParams.width = size.x/3;
        layoutParams.height = layoutParams.width * opt.outHeight / opt.outWidth;

        layoutParams.leftMargin = (int) (0.75f * size.x - layoutParams.width/2);
        layoutParams.topMargin = (int) (0.10f * size.y - layoutParams.height/2);

        int rand = random.nextInt(high-low) + low;
        TranslateAnimation translate1 =
                new TranslateAnimation(0.75f * size.x, 0.75f * size.x, 0.10f * size.y, 0.90f * size.y);
        translate1.setDuration(rand);
        translate1.setRepeatMode(Animation.RESTART);
        translate1.setRepeatCount(Animation.INFINITE);
        AlphaAnimation alpha1 = new AlphaAnimation(1f, 0.4f);
        alpha1.setDuration(rand);
        alpha1.setRepeatMode(Animation.REVERSE); // Originally, REVERSE
        alpha1.setRepeatCount(Animation.INFINITE);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(translate1);
        //animationSet.addAnimation(alpha1);
        drop1 = new ImageView(activity);
        drop1.setImageResource(R.drawable.rain_drop);

        darkCloudIV.setLayoutParams(layoutParams);
        drop1.startAnimation(animationSet);

        // Animation translate_left = AnimationUtils.loadAnimation(activity, R.anim.translate_cloud);
        // darkCloudIV.startAnimation(translate_left);

        /*float scaleRatio = context.getResources().getDisplayMetrics().density;


        Float scale = 0.75f;
        darkCloudIV.setScaleX(scale);
        darkCloudIV.setScaleY(scale);

        int dps = 100;
        int pixle = (int) (dps * scaleRatio + 0.5f);
        darkCloudIV.setX(pixle);*/


    }


    public ViewGroup addViews(ViewGroup layout) {
        layout.addView(drop1);
        layout.addView(darkCloudIV);
        return layout;
    }
}
