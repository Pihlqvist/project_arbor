package se.kth.projectarbor.project_arbor.view_objects;

import android.os.Handler;
import android.widget.ImageView;

import se.kth.projectarbor.project_arbor.R;

/**
 * Created by Patrik on 2017-05-16.
 */

public class LoadingView {
    private ImageView loading;
    private final Handler loadingHandler;
    private Integer[] loadingImages;

    public LoadingView(ImageView loading) {
        this.loading = loading;
        loadingHandler = new Handler();
        loadingImages = new Integer[]{R.drawable.phase_1, R.drawable.phase_3, R.drawable.phase_5, R.drawable.phase_7};
    }

    public void setLoadScreen(){
        Runnable runnable = new Runnable() {
            int loadingImgIndex = 0;
            public void run() {
                loading.setImageResource(loadingImages[loadingImgIndex]);
                loadingImgIndex++;
                if (loadingImgIndex >= loadingImages.length)
                    loadingImgIndex = 0;
                loadingHandler.postDelayed(this, 500);
            }
        };
        loadingHandler.postDelayed(runnable, 500);
    }
    public void stopLoadScreen(){
    }
}
