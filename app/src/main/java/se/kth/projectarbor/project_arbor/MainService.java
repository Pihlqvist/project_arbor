package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

public class MainService extends Service {
    public final static String TREE_DATA = "se.kth.projectarbor.project_arbor.intent.TREE_DATA";

    final static String TAG = "ARBOR";
    final static String filename = "user42.dat";

    // Times in seconds that the alarm will take to repeat the service
    public final static int ALARM_HOUR = 14;  // TODO: changed to min for testing
    public final static int ALARM_DAY = 24 * 60 * 60;

    // Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;
    public final static int MSG_UPDATE_HEALTH = 4;
    public final static int MSG_KM_DONE = 5;
    public final static int MSG_UPDATE_VIEW = 6;
    public final static int MSG_TREE_GAME = 7;
    public final static int MSG_PURCHASE = 8;

    public final static int MAIN_FOREGROUND = 111;

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
        Log.d("ARBOR_SERVICE", "Service onCreate()");
        List<Object> list = DataManager.readState(this, filename);
        loadState(list);
        // TODO: Define the order of (de)serializing objects
        environment = new Environment(getApplicationContext(), (Environment.Forecast[]) list.get(1));
        pedometer = new Pedometer(getApplicationContext(), userLength, userGender, totalDistance
                , tree.getTreePhase().getPhaseNumber());
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int msg = 0;
        if (intent.getExtras() != null) {
            msg = intent.getExtras().getInt("MESSAGE_TYPE", 0);
            Log.d(TAG, "MSG: " + msg);
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

            // Updates the tree every our, will lower the trees needs and set a timer to do it again
            case MSG_UPDATE_NEED:
                tree.update();
                sendToView();
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_HOUR * 1000), pendingIntent);

                DataManager.saveState(this, filename, tree,
                        environment.getForecasts(), pedometer.getTotalDistance());

                break;

            case MSG_UPDATE_HEALTH:
                // TODO: old now, might not be needed

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_DAY * 1000), pendingIntent);

                break;

            // The user have traveled 1 km and the user trees buffers will increase
            case MSG_KM_DONE:
                tree.bufferIncrease(environment.getWeather());
                pedometer.setPhaseNumber(tree.getTreePhase().getPhaseNumber());
                sendToView();

                break;

            // Update the tree view with new information
            case MSG_UPDATE_VIEW:
                sendToView();

                break;

            // Start the game, this is used when the tree is first created
            case MSG_TREE_GAME:
                startGame();

                break;

            case MSG_PURCHASE:
                tree.purchase((ShopTab.StoreItem)intent.getExtras().get("STORE_ITEM"));
                sendToView();
                DataManager.saveState(this, filename, tree,
                        environment.getForecasts(), pedometer.getTotalDistance());
                break;
        }

        return START_NOT_STICKY;
    }

    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        totalDistance = (Double) objects.get(2);
    }

    private void startForeground() {
        Intent resumeIntent = new Intent(this, MainUIActivity.class);
        PendingIntent resumePending = PendingIntent.getActivity(this, 0, resumeIntent, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_a);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_a)
                .setLargeIcon(bitmap)
                .setContentTitle("Arbor")
                .setContentText(getText(R.string.content_text))
                .setContentIntent(resumePending);

        startForeground(MAIN_FOREGROUND, notification.build());
    }


    // TODO: Fix this with bundle
    public void sendToView() {
        Log.d(TAG, "sendToView()");
        Intent intent = new Intent();

        intent.putExtra("WEATHER", environment.getWeather().toString());
        intent.putExtra("TEMP", environment.getTemp());
        intent.putExtra("SUN", tree.getSunLevel());
        intent.putExtra("WATER", tree.getWaterLevel());
        intent.putExtra("HP", tree.getHealth());
        intent.putExtra("PHASE", tree.getTreePhase().toString());

        intent.setAction(TREE_DATA);
        getApplicationContext().sendBroadcast(intent);
    }

    public void startGame() {
        Intent intentToActivity = new Intent(this, MainUIActivity.class);
        intentToActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intentToActivity.putExtra("WEATHER", environment.getWeather().toString());
        intentToActivity.putExtra("TEMP", environment.getTemp());
        intentToActivity.putExtra("SUN", tree.getSunLevel());
        intentToActivity.putExtra("WATER", tree.getWaterLevel());
        intentToActivity.putExtra("HP", tree.getHealth());
        intentToActivity.putExtra("PHASE", tree.getTreePhase().toString());

        startActivity(intentToActivity);
    }
}


