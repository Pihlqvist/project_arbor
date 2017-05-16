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
 * Created by Fredrik Pihlqvist and Lazar Cerovic on 2017-05-05.
 *
 * Creates a sun and light ray that is pointed on the tree.
 * Add the images by giving a layout and the same layout with the
 * images is returned.
 */

public class SunView {

    // Variables for sun and ray animations
    private final static float SUN_SIZE = 0.8f;
    private final static float RAY_WIDTH = 2f;
    private final static float RAY_HEIGHT = 1.2f;

    // Saved sun and ray images with animations
    private ImageView sunIV;
    private ImageView rayIV;


    public SunView(Activity activity) {

        RelativeLayout.LayoutParams sunLayout;
        RelativeLayout.LayoutParams rayLayout;

        // Gives width and height information about the available screen size
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        // Create and place Sun
        sunIV = new ImageView(activity);
        sunIV.setImageResource(R.drawable.sun);
        sunLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set the sun widht and height depending on the screen size
        sunLayout.width = (int) (size.x * SUN_SIZE);
        sunLayout.height = (int) (size.x * SUN_SIZE);

        // Places the sun in the top left corner
        sunLayout.leftMargin = -sunLayout.width/2;
        sunLayout.topMargin = -sunLayout.height/2;
        sunIV.setLayoutParams(sunLayout);


        // Create and place Ray
        rayIV = new ImageView(activity);
        rayIV.setImageResource(R.drawable.light_ray_360);
        rayLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Resize the ray to fit the screen and the sun
        rayLayout.width = (int) (size.x * RAY_WIDTH);
        rayLayout.height = (int) (size.x * RAY_HEIGHT);

        // Place the ray under the sun pointing towards the tree
        rayLayout.topMargin = (int)(-rayLayout.height * (0.2));
        rayLayout.leftMargin = (int)(-rayLayout.width * (0.065));
        rayIV.setLayoutParams(rayLayout);


        // Create and start animations
        Animation animSun = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
        Animation animRay = AnimationUtils.loadAnimation(activity, R.anim.rays_beeming);
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
