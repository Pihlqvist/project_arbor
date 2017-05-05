package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by fredrik on 2017-05-05.
 */

public class RainView {

    private ImageView darkCloudIV;
    private ImageView[] raindropsIV;

    private ConstraintLayout.MarginLayoutParams layoutParams;


    public RainView(Context context) {

        layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        darkCloudIV = new ImageView(context);
        darkCloudIV.setImageResource(R.drawable.rain_cloud_1);


        float scaleRatio = context.getResources().getDisplayMetrics().density;


        Float scale = 0.75f;
        darkCloudIV.setScaleX(scale);
        darkCloudIV.setScaleY(scale);

        int dps = 100;
        int pixle = (int) (dps * scaleRatio + 0.5f);
        darkCloudIV.setX(pixle);


        darkCloudIV.setLayoutParams(layoutParams);



    }


    public ConstraintLayout addViews(ConstraintLayout layout) {
        layout.addView(darkCloudIV);
        return layout;
    }
}
