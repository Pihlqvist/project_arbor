package se.kth.projectarbor.project_arbor.view_objects;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

/**
 * TODO: document your custom view class.
 */

public class TreeView extends View {
    private Movie mMovie;
    private long mMovieStart;
    private InputStream mStream;
    private int relTime = 0;
    private boolean mIsPlayingGif = false;

    private GifDecoder mGifDecoder;
    private Bitmap mTmpBitmap;
    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
            }
        }
    };
    public TreeView(Context context, InputStream stream) {
        super(context);

        mStream = stream;
        mMovie = Movie.decodeStream(mStream);
        playGif();

    }

    @Override
    protected void onDraw(final Canvas canvas) {

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        super.onDraw(canvas);
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        canvas.drawColor(Color.TRANSPARENT);

        new Thread(new Runnable() {
            public void run() {
                final int n = mGifDecoder.getFrameCount();
                final int ntimes = mGifDecoder.getLoopCount();
                int repetitionCounter = 0;
                do {
                    for (int i = 0; i < n; i++) {
                        mTmpBitmap = mGifDecoder.getFrame(i);
                        final int t = mGifDecoder.getDelay(i);
                        mHandler.post(mUpdateResults);
                        try {
                            Thread.sleep(t);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(ntimes != 0) {
                        repetitionCounter ++;
                    }
                } while (mIsPlayingGif && (repetitionCounter <= ntimes));
            }
        }).start();


/*        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0){
            mMovieStart = now;
        }
        relTime = (int)((now - mMovieStart) % mMovie.duration());
        mMovie.setTime(relTime);
        mMovie.draw(canvas, 100, 100);
        this.postInvalidateDelayed(200);*/
    }
    private void playGif (){
        mGifDecoder = new GifDecoder();
        mGifDecoder.read(mStream);
        mIsPlayingGif = true;
    }

}
