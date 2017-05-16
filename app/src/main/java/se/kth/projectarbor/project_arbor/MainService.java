package se.kth.projectarbor.project_arbor;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/*
* Created by Project Arbor
*
* This is the central hub of the project. All activity's communicate with this service
* as a middleman. You send messages with a key string structure, example: String: "MESSAGE_TYPE"
* Int: "3". And it will find this in its switch case structer and run that code.
*
 */

public class MainService extends Service {

    public final static String TREE_DATA = "se.kth.projectarbor.project_arbor.intent.TREE_DATA";
    private final static String TAG = "ARBOR_SERVICE";
    final static String filename = "user42.dat";

    // Times in seconds that the alarm will take to repeat the service
    public final static int ALARM_HOUR = 15;  // TODO: changed to min for testing
    public final static int ALARM_DAY = 24 * 60 * 60;

    // Messages to be used in Service. Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;
    public final static int MSG_KM_DONE = 5;
    public final static int MSG_UPDATE_VIEW = 6;
    public final static int MSG_TREE_GAME = 7;
    public final static int MSG_PURCHASE = 8;
    public final static int MSG_RESUME_HEAVY = 9;
    public final static int MSG_RESUME_LIGHT = 10;
    public final static int MSG_BOOT = 11;
    public final static int MAIN_FOREGROUND = 111;

    // MainService works with following components
    private AlarmManager alarmManager;
    private Pedometer pedometer;
    private Tree tree;
    private Environment environment;
    private double totalDistance; //distance to be stored in file and handled in mainservice
    private int totalStepCount; //distance to be stored in file and handled in mainservice
    // end

    // User information  // TODO: the user should change these themself
    private static double userLength = 1.8;
    private static Pedometer.Gender userGender = Pedometer.Gender.MALE;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service onCreate()");

        // Load essential information from IO
        List<Object> list = DataManager.readState(this, filename);
        loadState(list);

        // TODO: Define the order of (de)serializing objects
        // Instantiate objects that MainService will work with, information from previous
        // runtime are given by loadState() above
        environment = new Environment(getApplicationContext(), (Environment.Forecast[]) list.get(1));
        pedometer = new Pedometer(getApplicationContext(), userLength, userGender, totalDistance, totalStepCount
                , tree.getTreePhase().getPhaseNumber());
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Handle the message at the start.
        int msg = 0;
        if (intent.getExtras() != null) {
            msg = intent.getExtras().getInt("MESSAGE_TYPE", 0);
            Log.d(TAG, "MSG: " + msg);
        }

        //USED TO NOTIFY
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        // Depending on the msg a different action is taken
        break_point: switch (msg) {  // Break point needed for MSG_BOOT to break out of case
                                     // if tree died.

            // Start pedometer and start a foreground
            case MSG_START:
                pedometer.resetAndRegister();
                // TODO: Do we need to read here ?
                // List<Object> list = DataManager.readState(this, filename);
                // loadState(list);
                startForeground();
                break;

            // Stop pedometer and stop the foreground
            case MSG_STOP:
                pedometer.unregister();
                stopForeground(true);
                saveGame();
                break;

            // Does activity related resume HEAVY indicates that we need to setup pedometer
            case MSG_RESUME_HEAVY:
                pedometer.register();
                sendToView();
                startForeground();
                break;

            // Does activity related resume
            case MSG_RESUME_LIGHT:
                sendToView();
                break;

            // Updates the tree, every hour. Will lower the trees needs and set a timer to do it again
            case MSG_UPDATE_NEED:
                tree.update();
                sendToView();
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_HOUR * 1000), pendingIntent);
                saveGame();
                break;

            // The user have traveled 1 km and the user trees buffers will increase
            case MSG_KM_DONE:
                tree.bufferIncrease(environment.getWeather());
                pedometer.setPhaseNumber(tree.getTreePhase().getPhaseNumber());
                sendToView();
                saveGame();
                break;

            // Update the tree view with new information
            case MSG_UPDATE_VIEW:
                sendToView();
                break;

            // Start the game, this is used when the tree is first created
            case MSG_TREE_GAME:
                startGame();
                break;

            // Store sends this message, updates the tree with the right item
            case MSG_PURCHASE:
                tree.purchase((ShopTab.StoreItem)intent.getExtras().get("STORE_ITEM"));
                sendToView();
                saveGame();
                break;

            case MSG_BOOT:
                long now = System.currentTimeMillis();
                SharedPreferences sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);
                long then = sharedPreferences.getLong("SHUTDOWN_TIME", now); // second argument is important
                long interval = now - then;
                Log.d("ARBOR_AGE", "interval millisec: " + interval);
                // TODO: Change back to interval/1000/60/60
                interval = 4 * interval/1000/60; // number of hours
                Log.d("ARBOR_AGE", "interval: " + interval);
                Log.d("ARBOR_AGE", "Inside case MSG_BOOT");

                // Do the update as many times as needed.
                for (int i = 0; i < interval; i++) {
                    boolean alive = tree.update();
                    if (!alive) {
                        /* TODO: Insert this from deathOfTree when is merged
                        Log.d("ARBOR_MSG_UPDATE_NEED", "alive is false");
                        sharedPreferences.edit().putBoolean("TREE_ALIVE", false).apply();
                        // TODO: Check if it's enough to unregister or if reset() is needed as well.
                        // Pedometer will stop updating km to MainService
                        pedometer.unregister();
                        Intent intentTreeDeath = new Intent();
                        intentTreeDeath.setAction(TREE_DEAD);
                        MainService.this.sendBroadcast(intentTreeDeath);
                        break break_point;
                        */
                    }
                }
                sendToView();

                Log.d("ARBOR_AGE", "WaterLevel: " + tree.getWaterLevel() + ", SunLevel: " + tree.getSunLevel());
                // Start tree update 1 hour after booting.
                Intent intentToUpdate = new Intent(this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MSG_UPDATE_NEED);
                PendingIntent pendingToUpdate = PendingIntent.getService(this, 0, intentToUpdate, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (ALARM_HOUR * 1000), pendingToUpdate);
//                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
//                        + (ALARM_HOUR * 1000), pendingToUpdate);
                break;
        }
        return START_NOT_STICKY;
    }
    // Load tree and tDistance from and stepcount from IO/file
    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        totalDistance = (Double) objects.get(2);
        totalStepCount = (int) objects.get(3);
    }
    // Foreground is created here
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
    // Update the views in the MainActivity via a broadcast
    public void sendToView() {
        Log.d(TAG, "sendToView()");
        Intent intent = new Intent();

        intent.putExtra("WEATHER", environment.getWeather());
        intent.putExtra("TEMP", environment.getTemp());
        intent.putExtra("SUN", tree.getSunLevel());
        intent.putExtra("WATER", tree.getWaterLevel());
        intent.putExtra("HP", tree.getHealth());
        intent.putExtra("PHASE", tree.getTreePhase().toString());
        intent.putExtra("TOTALKM", pedometer.getTotalDistance());
        intent.putExtra("TOTALSTEPS", pedometer.getTotalStepCount());
        // TODO Implement in the receiver
        intent.putExtra("AGE",
                System.currentTimeMillis() - getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE).getLong("TREE_START_TIME", 0));
        intent.setAction(TREE_DATA);
        getApplicationContext().sendBroadcast(intent);
    }
    // Start MainActivity and give it the information it needs via an intent
    private void startGame() {
        Intent intentToActivity = new Intent(this, MainUIActivity.class);
        intentToActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intentToActivity.putExtra("WEATHER", environment.getWeather());
        intentToActivity.putExtra("TEMP", environment.getTemp());
        intentToActivity.putExtra("SUN", tree.getSunLevel());
        intentToActivity.putExtra("WATER", tree.getWaterLevel());
        intentToActivity.putExtra("HP", tree.getHealth());
        intentToActivity.putExtra("PHASE", tree.getTreePhase().toString());
        intentToActivity.putExtra("TOTALKM", pedometer.getTotalDistance());
        intentToActivity.putExtra("TOTALSTEPS", pedometer.getTotalDistance());
        intentToActivity.putExtra("AGE",
                System.currentTimeMillis() - getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE).getLong("TREE_START_TIME", 0));
        startActivity(intentToActivity);
    }
    // Save everything, this is so that we save essential information when the service dies
    private void saveGame() {
        DataManager.saveState(this, filename, tree,
                environment.getForecasts(), pedometer.getTotalDistance(), pedometer.getTotalStepCount());
    }
    //TODO: Make a pretty way of capturing expressions
    @Override
    public void onDestroy(){
        saveGame();
    }

    /*  CURRENTLY NOT IN USE
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    */
}


