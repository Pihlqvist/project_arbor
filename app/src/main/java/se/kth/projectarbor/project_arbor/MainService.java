package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class MainService extends Service {
    public final static String TREE_DATA = "se.kth.projectarbor.project_arbor.intent.TREE_DATA";

    final static String TAG = "ARBOR";
    final static String filename = "user42.dat";

    // Times in seconds that the alarm will take to repeat the service
    public final static int ALARM_HOUR = 60 * 60;  // TODO: changed to min for testing
    public final static int ALARM_DAY = 24 * 60 * 60;
    public final static int ALARM_TEST = 7;

    // Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;
    public final static int MSG_UPDATE_HEALTH = 4;
    public final static int MSG_KM_DONE = 5;
    public final static int MSG_UPDATE_VIEW = 6;

    // MainService works with following components
    private Pedometer pedometer;
    private Tree tree;
    private Environment environment;
    private AlarmManager alarmManager;
    private double totalDistance;
    // end

    // User information
    private static double userLength = 1.8;
    private static Pedometer.Gender userGender = Pedometer.Gender.MALE;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        List<Object> list = DataManager.readState(this, filename);
        loadState(list);
        // TODO: Define the order of (de)serializing objects
        environment = new Environment(getApplicationContext(), (Environment.Forecast[]) list.get(1));
        pedometer = new Pedometer(getApplicationContext(), userLength, userGender, totalDistance);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


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
                pedometer.resetAndRegister();
                List<Object> list = DataManager.readState(this, filename);
                loadState(list);
                startForeground();
                break;

            // Stop location manager and stop the foreground
            case MSG_STOP:
                pedometer.unregister();
                stopForeground(true);
                DataManager.saveState(this, filename, tree,
                        environment.getForecasts(), pedometer.getTotalDistance());

                break;

            case MSG_UPDATE_NEED:
                tree.update();
                sendToView();
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_HOUR * 1000), pendingIntent);

                // TODO: Think about it... How to make the environment object persistent?
                DataManager.saveState(this, filename, tree,
                        environment.getForecasts(), pedometer.getTotalDistance());

                break;

            case MSG_UPDATE_HEALTH:
                // TODO: update the tree and give the information to the reciver in the activity

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_DAY * 1000), pendingIntent);

                break;

            case MSG_KM_DONE:
                tree.bufferIncrease(environment.getWeather());
                sendToView();

                break;

            case MSG_UPDATE_VIEW:
                sendToView();
                break;
        }

        return START_NOT_STICKY;
    }

    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        totalDistance = (Double) objects.get(2);
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


    // TODO: Fix this with bundle
    public void sendToView() {
        Log.d(TAG, "sendToView()");
        Intent intent = new Intent();
        Bundle extras = new Bundle();
        extras.putString("WEATHER", environment.getWeather().toString());
        extras.putDouble("TEMP", environment.getTemp());
        extras.putInt("SUN", tree.getSunLevel());
        extras.putInt("WATER", tree.getWaterLevel());
        extras.putInt("HP", tree.getHealth());
        extras.putString("PHASE", tree.getTreePhase().toString());
        intent.putExtras(extras);
        intent.setAction(TREE_DATA);

        getApplicationContext().sendBroadcast(intent);
    }
}

