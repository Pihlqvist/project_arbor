package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class MainService extends Service {
    final static String TAG = "ARBOR";
    final static String filename = "user42.dat";

    // Times in seconds that the alarm will take to repeat the service
    private final static int ALARM_HOUR = 60 * 60;
    private final static int ALARM_DAY = 24 * 60 * 60;

    // Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;
    public final static int MSG_UPDATE_HEALTH = 4;
    public final static int MSG_KM_DONE = 5;

    // MainService works with following components
    private LocationManager locationManager;
    private Tree tree;
    private Environment environment;
    private AlarmManager alarmManager;
    // end

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // IS_NEW
        Log.d(TAG, "onCreate()");
        locationManager = new LocationManager(this, 10000, (float) 2.5, (float) 80.0);
        List<Object> list = DataManager.readState(this, filename);
        loadState(list);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand()");

        // NEW implementation

        int msg = 0;
        if (intent.getExtras() != null) {
            msg = intent.getExtras().getInt("MESSAGE_TYPE", 0);
            Log.d(TAG, "msg: " + msg);
        }

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);


        // Depending on the msg a different action is taken
        switch (msg) {

            // Start location manager and start a foreground
            case MSG_START:
                locationManager.connect();
                List<Object> list = DataManager.readState(this, filename);
                loadState(list);
                startForeground();
                break;

            // Stop location manager and stop the foreground
            case MSG_STOP:
                locationManager.disconnect();
                stopForeground(true);
                DataManager.saveState(this, filename, tree,
                        locationManager.getTotalDistance(), environment);

                break;

            case MSG_UPDATE_NEED:
                // TODO: update the tree and give the information to the reciver in the activity

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_HOUR * 1000), pendingIntent);

                // TODO: Think about it... How to make the environment object persistent?
                DataManager.saveState(this, filename, tree,
                        locationManager.getTotalDistance(), environment);

                break;

            case MSG_UPDATE_HEALTH:
                // TODO: update the tree and give the information to the reciver in the activity

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_DAY * 1000), pendingIntent);

                break;

            case MSG_KM_DONE:
                // TODO: now we have walked 1 km and we want to update tree with it
                Environment.Weather weather = environment.getWeather();
                //tree.updateBuffer(weather);

                break;
        }



        // End of new implementation


        return START_NOT_STICKY;
    }

    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        Float distance = (Float) objects.get(1);
        environment = (Environment) objects.get(2);
        locationManager.setTotalDistance(distance);
    }

    private void startForeground() {
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.content_text))
                //.setSmallIcon(R.drawable.icon)
                //.setContentIntent(pendingIntent)
                //.setTicker(getText(R.string.ticker_text))
                .getNotification();

        startForeground(1, notification);
    }

    public Float getDist() {
        // TODO: is this useful?, look at it later and se if a better implementation can be found
        return locationManager.getTotalDistance();
    }
}

