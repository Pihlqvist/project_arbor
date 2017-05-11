package se.kth.projectarbor.project_arbor.view_objects;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import se.kth.projectarbor.project_arbor.R;

/**
 * Created by fredrik on 2017-05-05.
 */

public class SunView {

    private ImageView sunIV;
    private ImageView rayIV;

    private Animation animSun;
    private Animation animRay;

    private RelativeLayout.LayoutParams sunLayout;
    private RelativeLayout.LayoutParams rayLayout;


    public SunView(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Create and place Sun
        sunIV = new ImageView(activity);
        sunIV.setImageResource(R.drawable.sun);

        sunLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        sunLayout.width = (int) (size.x * 0.8);
        sunLayout.height = (int) (size.x * 0.8);

        sunLayout.leftMargin = -sunLayout.width/2;
        sunLayout.topMargin = -sunLayout.height/2;

        sunIV.setLayoutParams(sunLayout);

        // Create and place Ray
        rayIV = new ImageView(activity);
        rayIV.setImageResource(R.drawable.light_ray_360);

        rayLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        rayLayout.width = (int) (size.x * 2);
        rayLayout.height = (int) (size.x * 1.2);

        rayLayout.topMargin = (int)(-rayLayout.height * (0.2));
        rayLayout.leftMargin = (int)(-rayLayout.width * (0.065));

        rayIV.setLayoutParams(rayLayout);

        // Create and start animations
        animSun = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        animRay = AnimationUtils.loadAnimation(activity, R.anim.rays_beeming);

        sunIV.startAnimation(animSun);
        rayIV.startAnimation(animRay);


    }


    // Add the views in this class to a specific layout
    public ViewGroup addViews(ViewGroup layout) {
        ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        layout.setLayoutParams(layoutParams);
        layout.addView(rayIV);
        layout.addView(sunIV);

        return layout;
    }



}
