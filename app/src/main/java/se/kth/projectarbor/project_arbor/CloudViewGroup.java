package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by fredrik on 2017-05-04.
 */

public class CloudViewGroup extends ViewGroup {

    private Drawable cloud1;
    private Drawable cloud2;
    private Drawable cloud3;


    public CloudViewGroup(Context context) {
        super(context);
    }

    // Position all children within this layout.
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }
}
