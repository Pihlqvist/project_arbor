package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by fredrik on 2017-05-05.
 */

public class SunView {

    private ImageView sunIV;
    private ImageView rayIV;

    private Animation animSun;
    private Animation animRay;

    private ConstraintLayout.LayoutParams layoutParams;



    public SunView(Context context) {

        rayIV = new ImageView(context);
        sunIV = new ImageView(context);

        rayIV.setImageResource(R.drawable.light_ray_360);
        sunIV.setImageResource(R.drawable.sun_360);


        layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        rayIV.setLayoutParams(layoutParams);
        sunIV.setLayoutParams(layoutParams);

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

        animSun = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animRay = AnimationUtils.loadAnimation(context, R.anim.rays_beeming);

        sunIV.startAnimation(animSun);
        rayIV.startAnimation(animRay);



    }


    public ConstraintLayout addViews(ConstraintLayout layout) {
        layout.addView(rayIV);
        layout.addView(sunIV);

        return layout;
    }



}
