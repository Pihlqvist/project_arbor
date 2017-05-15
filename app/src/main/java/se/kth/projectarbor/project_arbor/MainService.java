package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.List;
import se.kth.projectarbor.project_arbor.weather.Environment;

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
    public final static String WEATHER_DATA = "se.kth.projectarbor.project_arbor.intent.WEATHER_DATA";
    public final static String TREE_DEAD = "se.kth.projectarbor.project_arbor.intent.TREE_DEAD";
    private final static String TAG = "ARBOR_SERVICE";
    final static String filename = "user42.dat";

    // Times in seconds that the alarm will take to repeat the service
    public final static int ALARM_HOUR = 10; // TODO: Change back to 60 * 60

    // Messages to be used in Service. Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;
    public final static int MSG_KM_DONE = 5;
    public final static int MSG_UPDATE_VIEW = 6;
    public final static int MSG_TREE_GAME = 7;
    public final static int MSG_PURCHASE = 8;
    public final static int MSG_UPDATE_WEATHER_VIEW = 12;

    public final static int MSG_RESUME_HEAVY = 9;
    public final static int MSG_RESUME_LIGHT = 10;
    public final static int MAIN_FOREGROUND = 111;

    // MainService works with following components
    private Pedometer pedometer;
    private Tree tree;
    private Environment environment;
    private int totalStepCount; //steps to be stored in file and handled in mainservice
    private AlarmManager alarmManager;
    private double totalDistance; //distance to be stored in file and handled in mainservice
    private Environment.Weather lastWeather;
    private double lastTemperature;
    private SharedPreferences sharedPreferences;

    // end

    // User information  // TODO: the user should change these thyself  (Fredrik)
    private final Object lock = new Object();

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

        // Used to know whether tree is alive or not.
        sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);
        // Load essential information from IO
        List<Object> list = DataManager.readState(this, filename);
        loadState(list);

        // TODO: Define the order of (de)serializing objects (Ramzin)
        // Instantiate objects that MainService will work with, information from previous
        // runtime are given by loadState() above
        environment = new Environment(getApplicationContext(), (Environment.Forecast[]) list.get(1));
        pedometer = new Pedometer(getApplicationContext(), userLength, userGender, totalDistance, totalStepCount
                , tree.getTreePhase().getPhaseNumber());
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        lastWeather = Environment.Weather.NOT_AVAILABLE;
        lastTemperature = Double.NaN;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Handle the message at the start.
        // USED FOR TESTING
//        for (int i = 0; i < 5; i++) // TODO: Remove for after finishing testing
//            tree.update();

        int msg = 0;
        if (intent.getExtras() != null) {
            msg = intent.getExtras().getInt("MESSAGE_TYPE", 0);
            Log.d(TAG, "MSG: " + msg);
        }

        //USED TO NOTIFY
        PendingIntent pendingIntent;
        // Depending on the msg a different action is taken
        switch (msg) {

            // Start pedometer and start a foreground
            case MSG_START:
                pedometer.resetAndRegister();
                // TODO: Do we need to read here ? (Fredrik)
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
                pedometer.register();  // TODO: testing without
                sendToView();
                sendDistanceInfo();
                startForeground();
                break;

            // Does activity related resume
            case MSG_RESUME_LIGHT:
                sendToView();
                break;

            // Updates the tree, every hour. Will lower the trees needs and set a timer to do it again
            case MSG_UPDATE_NEED:
                boolean alive = tree.update();
                if (alive) {
                    sendToView();

                    pendingIntent = PendingIntent.getService(this, 0, intent, 0);

                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + (ALARM_HOUR * 1000), pendingIntent);
                    saveGame();
                }
                else {
                    sharedPreferences.edit().putBoolean("TREE_ALIVE", false).apply();
                    // TODO: Check if it's enough to unregister or if reset() is needed as well.
                    // Pedometer will stop updating km to MainService
                    pedometer.unregister();
                    Intent intentTreeDeath = new Intent();
                    intentTreeDeath.setAction(TREE_DEAD);
                    MainService.this.sendBroadcast(intentTreeDeath);
                }

                break;

            // The user have traveled 1 km and the user trees buffers will increase
            case MSG_KM_DONE:
                synchronized (lock) {
                    tree.bufferIncrease(lastWeather);
                }

                pedometer.setPhaseNumber(tree.getTreePhase().getPhaseNumber());
                sendToView();
                saveGame();
                break;

            // Update the tree view with new information
            // TODO: Same as UPDATE_LIGHT, should this be ?  (Fredrik)
            case MSG_UPDATE_VIEW:
                sendToView();
                break;

            // Start the game, this is used when the tree is first created
            case MSG_TREE_GAME:
                // Used to stop updates and show death screen when tree dies
                sharedPreferences.edit().putBoolean("TREE_ALIVE", true);
                pedometer.resetAll();

                startGame();
                Intent weatherIntent = new Intent(MainService.this.getApplicationContext(), MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_WEATHER_VIEW);
                PendingIntent weatherPendingIntent = PendingIntent.getService(MainService.this, 1, weatherIntent, 0);

                sendWeatherToView(weatherPendingIntent);
                Log.d("ARBOR_WEATHER", "Exiting MSG_TREE_GAME");

                break;

            // Store sends this message, updates the tree with the right item
            case MSG_PURCHASE:
                tree.purchase((ShopTab.StoreItem)intent.getExtras().get("STORE_ITEM"));
                sendToView();
                saveGame();
                break;

            // Update the weather and temperature fields
            case MSG_UPDATE_WEATHER_VIEW:
                Log.d("ARBOR_WEATHER", "Retrieved MSG_UPDATE_WEATHER_VIEW");

                pendingIntent = PendingIntent.getService(this.getApplicationContext(), 1, intent, 0);

                sendWeatherToView(pendingIntent);
                break;
        }
        return START_NOT_STICKY;
    }

    // Load tree and tDistance from and stepcount from IO/file
    private void loadState(List<Object> objects) {
        if (objects.size() < 4) {
            Log.e(TAG, "objects in loadState was below 4, so new variables was made");
            tree = new Tree();
            totalDistance = 0;
            totalStepCount = 0;
        } else {
            if (objects.get(0) != null && objects.get(0).getClass() == Tree.class)  {
                tree = (Tree) objects.get(0);
            } else {
                tree = new Tree();
                Log.e(TAG, "Tree was not found in file: " + filename + ", tree = new Tree()");
            }
            if (objects.get(2) != null /* && objects.get(2).getClass() == double.class */) { //TODO: getClass not working
                totalDistance = (double) objects.get(2);
            } else {
                totalDistance = 0;
                Log.e(TAG, "totalDistance was not found in file: " + filename + ", totalDistance = 0");
            }
            if (objects.get(3) != null /* && objects.get(3).getClass() == int.class */) { //TODO: getClass not working
                totalStepCount = (int) objects.get(3);
            } else {
                totalStepCount = 0;
                Log.e(TAG, "totalStepCount was not found in file: " + filename + ", totalStepCount = 0");
            }
        }


    }

    // Foreground is created here
    private void startForeground() {
        // TODO: send information about weather (Fredrik)
        Intent resumeIntent = new Intent(this, MainUIActivity.class);
        resumeIntent = putTreeInformation(resumeIntent);
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

    // TODO: Fix this with bundle (?)
    // Update the views in the MainActivity via a broadcast
    public void sendToView() {
        Intent intent = new Intent();
        intent = putTreeInformation(intent);
        intent.setAction(TREE_DATA);
        getApplicationContext().sendBroadcast(intent);
    }

    // Start MainActivity and give it the information it needs via an intent
    private void startGame() {
        Intent intent = new Intent(this, MainUIActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent = putTreeInformation(intent);
        startActivity(intent);
    }

    // Save everything, this is so that we save essential information when the service dies
    private void saveGame() {
        DataManager.saveState(this, filename, tree,
                environment.getForecasts(), pedometer.getTotalDistance(), pedometer.getTotalStepCount());
    }

    //TODO: Make a pretty way of capturing expressions  (?)
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

    private Intent putTreeInformation(Intent intent) {
        intent.putExtra("SUN", tree.getSunLevel());
        intent.putExtra("WATER", tree.getWaterLevel());
        intent.putExtra("HP", tree.getHealth());
        intent.putExtra("PHASE", tree.getTreePhase());
        intent.putExtra("TOTALKM", pedometer.getTotalDistance());
        intent.putExtra("TOTALSTEPS", pedometer.getTotalStepCount());
        return intent;
    }

    private Intent putWeatherInformation(Intent intent) {
        intent.putExtra("WEATHER", environment.getWeather());
        intent.putExtra("TEMP", environment.getTemp());
        return intent;
    }

    private class AsyncTaskRunner extends AsyncTask<PendingIntent, Void, PendingIntent> {

        @Override
        protected PendingIntent doInBackground(PendingIntent... params) {
            Environment.Weather newWeather = environment.getWeather();
            double newTemperature = environment.getTemp();

            synchronized (lock) {
                lastWeather = newWeather;
                lastTemperature = newTemperature;

                Log.d("ARBOR_WEATHER", lastWeather.toString() + ", temp=" + lastTemperature);
            }

            Intent intent = new Intent();
            intent.putExtra("WEATHER", lastWeather);
            intent.putExtra("TEMP", lastTemperature);
            intent.setAction(WEATHER_DATA);
            MainService.this.sendBroadcast(intent);

            return params[0];
        }


        @Override
        protected void onPostExecute(PendingIntent pendingIntent) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + (ALARM_HOUR/4*1000), pendingIntent);

            Log.d("ARBOR_WEATHER", "Exiting onPostExecute()");
        }
    }

    private void sendWeatherToView(final PendingIntent pendingIntent) {
        new AsyncTaskRunner().execute(pendingIntent);
    }

    private void sendDistanceInfo() {
        Intent intent = new Intent();
        intent.setAction(Pedometer.DISTANCE_BROADCAST);
        intent.putExtra("DISTANCE", pedometer.getSessionDistance());
        intent.putExtra("STEPCOUNT", pedometer.getSessionStepCount());
        MainService.this.sendBroadcast(intent);

    }
}


