package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by fredrik on 2017-05-04.
 */

public class CloudView extends View {

    private ImageView[] clouds;
    private ConstraintLayout.LayoutParams layoutParams;
    private Float randomFloat;
    private int randomInt1;
    private int randomInt2;
    private Random random;

    private Float SCALE_MIN = (float) 0.45;
    private Float SCALE_MAX = (float) 0.55;

    private int POSITION_TOP = 1;
    private int POSITION_LEFT = 1;
    private int POSITION_RIGHT = 1;
    private int POSITION_BOTTOM = 1;


    public CloudView(Context context) {
        super(context);

        random = new Random();
        clouds = new ImageView[3];


        layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i=0; i<clouds.length; i++) {
            clouds[i] = new ImageView(context);
            randomFloat = getRandFloat(SCALE_MIN, SCALE_MAX);
            clouds[i].setScaleX(randomFloat);
            clouds[i].setScaleY(randomFloat);

            randomInt1 = getRandInt(0,500);
            randomInt2 = getRandInt(0,500);
            clouds[i].setPadding(randomInt2,randomInt1,randomInt1,randomInt2);
            clouds[i].setLayoutParams(layoutParams);
        }



    }


    public ConstraintLayout addViews(ConstraintLayout layout) {
        for (ImageView imageView : clouds) {
            layout.addView(imageView);
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
