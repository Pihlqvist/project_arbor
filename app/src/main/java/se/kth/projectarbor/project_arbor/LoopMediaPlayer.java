package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Patrik on 2017-05-14.
 */
public class LoopMediaPlayer {

    private Context mContext = null;
    private int mResId = 0;
    private int mCounter = 1;
    private float mVolume = 1;
    private MediaPlayer mCurrentPlayer = null;
    private MediaPlayer mNextPlayer = null;

    public static LoopMediaPlayer create(Context context, int resId) {
        return new LoopMediaPlayer(context, resId);
    }

    private LoopMediaPlayer(Context context, int resId) {
        mContext = context;
        mResId = resId;

        mCurrentPlayer = MediaPlayer.create(mContext, mResId);
        mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mCurrentPlayer.start();
            }
        });

        createNextMediaPlayer();
    }

    private void createNextMediaPlayer() {
        mNextPlayer = MediaPlayer.create(mContext, mResId);
        mNextPlayer.setVolume(mVolume, mVolume);
        mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
        mCurrentPlayer.setOnCompletionListener(onCompletionListener);
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.release();
            mCurrentPlayer = mNextPlayer;

            createNextMediaPlayer();
        }
    };
    public void onPause(){
        mCurrentPlayer.pause();
    }
    public void onResume(){
        mCurrentPlayer.start();
    }
    public void setVolume(float f){
        mCurrentPlayer.setVolume(f, f);
        mVolume = f;
    }
}