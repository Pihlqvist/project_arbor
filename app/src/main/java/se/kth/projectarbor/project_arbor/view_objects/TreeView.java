package se.kth.projectarbor.project_arbor.view_objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import java.io.InputStream;

/**
 * TODO: document your custom view class.
 */


public class TreeView extends View {
    private long mMovieStart;
    private Movie mMovie;
    private InputStream mStream;
    public TreeView(Context context, InputStream is) {
        super(context);
        mStream = is;
        mMovie = Movie.decodeStream(is);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        final int relTime = (int)((now - mMovieStart) % mMovie.duration());
        mMovie.setTime(0);
        Log.d("TREEVIEW", Integer.toString(relTime));
        mMovie.draw(canvas, 100, 100);
        this.postInvalidateDelayed(1000);
    }
}
