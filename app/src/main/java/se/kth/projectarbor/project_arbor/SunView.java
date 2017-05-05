package se.kth.projectarbor.project_arbor;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by fredrik on 2017-05-05.
 */

public class SunView {

    private ImageView sunIV;
    private ImageView rayIV;

    private Animation animSun;
    private Animation animRay;

    private RelativeLayout.LayoutParams layoutParams;



    public SunView(Activity activity) {

        // rayIV = new ImageView(activity);
        sunIV = new ImageView(activity);

        // rayIV.setImageResource(R.drawable.light_ray_360);
        sunIV.setImageResource(R.drawable.sun_360);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        layoutParams.width = size.x;
        layoutParams.height = size.x;

        layoutParams.leftMargin = -layoutParams.width/2;
        layoutParams.topMargin = -layoutParams.height/2;

        // rayIV.setLayoutParams(layoutParams);
        sunIV.setLayoutParams(layoutParams);

        /*
        Float scale = 0.9f;
        sunIV.setScaleX(scale);
        sunIV.setScaleY(scale);

        sunIV.setX(-295f);
        sunIV.setY(-295f);

        scale = 1.65f;
        rayIV.setScaleY(scale);
        rayIV.setScaleX(scale);

        rayIV.setX(75f);
        rayIV.setY(20f);

        animSun = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        animRay = AnimationUtils.loadAnimation(activity, R.anim.rays_beeming);

        sunIV.startAnimation(animSun);
        rayIV.startAnimation(animRay);
        */

    }


    public ViewGroup addViews(ViewGroup layout) {
        // layout.addView(rayIV);
        layout.addView(sunIV);

        return layout;
    }



}
