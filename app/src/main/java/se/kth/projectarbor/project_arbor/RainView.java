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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by fredrik on 2017-05-05.
 */

public class RainView {

    private ImageView darkCloudIV;
    private ImageView[] raindropsIV;

    private RelativeLayout.LayoutParams layoutParams;


    public RainView(Activity activity) {
        darkCloudIV = new ImageView(activity);
        darkCloudIV.setImageResource(R.drawable.rain_cloud_1);

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(activity.getResources(), R.drawable.rain_cloud_1, opt);

        layoutParams.width = size.x/3;
        layoutParams.height = layoutParams.width * opt.outHeight / opt.outWidth;

        darkCloudIV.setLayoutParams(layoutParams);

        Animation translate_left = AnimationUtils.loadAnimation(activity, R.anim.translate_cloud);
        darkCloudIV.startAnimation(translate_left);

        /*float scaleRatio = context.getResources().getDisplayMetrics().density;


        Float scale = 0.75f;
        darkCloudIV.setScaleX(scale);
        darkCloudIV.setScaleY(scale);

        int dps = 100;
        int pixle = (int) (dps * scaleRatio + 0.5f);
        darkCloudIV.setX(pixle);*/
    }


    public ViewGroup addViews(ViewGroup layout) {
        layout.addView(darkCloudIV);
        return layout;
    }
}
