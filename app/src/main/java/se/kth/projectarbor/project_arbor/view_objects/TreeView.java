package se.kth.projectarbor.project_arbor.view_objects;

/**
 * Created by Patrik on 2017-05-15.
 */

        import android.content.Context;

        import android.graphics.Bitmap;

        import android.graphics.BitmapFactory;
        import android.graphics.Color;
        import android.graphics.drawable.Drawable;
        import android.os.Handler;

        import android.util.AttributeSet;

        import android.util.Log;

        import android.widget.ImageView;

        import se.kth.projectarbor.project_arbor.R;


public class TreeView extends ImageView implements Runnable {

    public boolean grow;

    private int frameCount;

    final int mod;

    private static final String TAG = "GifDecoderView";

    private GifDecoder gifDecoder;

    private Bitmap tmpBitmap;

    private final Handler handler = new Handler();

    private boolean animating = false;

    private Thread animationThread;
    public boolean firstime;

    private final Runnable updateResults = new Runnable() {

        @Override

        public void run() {
            Log.d("PATRIK", "UPDATE COMPLETE");
            if (tmpBitmap != null && !tmpBitmap.isRecycled()) {

                setBackgroundResource(R.color.colorTransparent);
                setImageBitmap(tmpBitmap);
                setScaleX(2f);
                setScaleY(2f);

            }

        }

    };



    public TreeView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        grow  = false;
        frameCount = 0;
        mod = 0;
        firstime = false;
    }



    public TreeView(final Context context) {

        super(context);
        grow = false;
        firstime = true;
        frameCount = 0;
        mod = 0;
        tmpBitmap = (BitmapFactory.decodeResource(getResources(), R.drawable.grown_tree));
        handler.post(updateResults);
    }



    public void setBytes(final byte[] bytes) {
        Log.d("PATRIK", "DECODE STARTED");
        gifDecoder = new GifDecoder();
        Log.d("PATRIK", "" + gifDecoder);
        Thread async = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    gifDecoder.read(bytes);
                    Log.d("PATRIK", "DECODE COMPLETE");
                    Log.d("PATRIK", "" + gifDecoder);
                    startAnimation();
                } catch (final OutOfMemoryError e) {
                    Log.d("PATRIK", "EX");
                    gifDecoder = null;


                }
            }
        });
        async.start();



        if (canStart()) {

            animationThread = new Thread(this);

            animationThread.start();

        }

    }



    public void startAnimation() {
        Log.d("PATRIK", "START COMPLETE");
        animating = true;



        if (canStart()) {

            animationThread = new Thread(this);

            animationThread.start();

        }

    }



    public void stopAnimation() {

        animating = false;



        if (animationThread != null) {

            animationThread.interrupt();

            animationThread = null;

        }

    }



    private boolean canStart() {

        return animating && gifDecoder != null && animationThread == null;

    }



    @Override

    public void run() {
            Log.d("PATRIK", "RUN COMPLETE");
            final int n = gifDecoder.getFrameCount() / 3;
            animating = false;
            do {
                for (int i = 0; i < n; i++) {
                    if(i==(n-1)){
                        i=i-2;
                        try {

                            tmpBitmap = gifDecoder.getNextFrame();

                            handler.post(updateResults);
                            frameCount++;

                        } catch (final ArrayIndexOutOfBoundsException e) {

                            Log.w(TAG, e);

                        } catch (final IllegalArgumentException e) {

                            Log.w(TAG, e);

                        }
                        gifDecoder.devance();
                        try {

                            Thread.sleep(gifDecoder.getNextDelay());

                        } catch (final InterruptedException e) {

                            // suppress

                        }
                    }else {
                        try {

                            tmpBitmap = gifDecoder.getNextFrame();

                            handler.post(updateResults);
                            frameCount++;

                        } catch (final ArrayIndexOutOfBoundsException e) {

                            Log.w(TAG, e);

                        } catch (final IllegalArgumentException e) {

                            Log.w(TAG, e);

                        }
                        gifDecoder.advance();
                        try {

                            Thread.sleep(gifDecoder.getNextDelay());

                        } catch (final InterruptedException e) {

                            // suppress

                        }
                    }

                }

            } while (animating);
    }
    public void setGrow(){
        grow = true;
    }

}
