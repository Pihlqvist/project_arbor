package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.support.v4.app.TaskStackBuilder;
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
    public final static int ALARM_HOUR = 10;  // TODO 60 * 60

    // Messages to be used in Service. Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;
    public final static int MSG_KM_DONE = 5;
    public final static int MSG_UPDATE_VIEW = 6;
    public final static int MSG_TREE_GAME = 7;
    public final static int MSG_PURCHASE = 8;
    public final static int MSG_UPDATE_WEATHER_VIEW = 12;
    public final static int MSG_BOOT = 11;
    public final static int MSG_RESUME_HEAVY = 9;
    public final static int MSG_RESUME_LIGHT = 10;
    public final static int MAIN_FOREGROUND = 111;
    public final static int MSG_USER_INPUT = 42;
    public final static int MSG_RESUME_TREE_GAME = 41;

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
        readUserSettings();

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
        int msg = 0;
        if (intent.getExtras() != null) {
            msg = intent.getExtras().getInt("MESSAGE_TYPE", 0);
            Log.d(TAG, "MSG: " + msg);
        }

        //USED TO NOTIFY
        PendingIntent pendingIntent;
        // Depending on the msg a different action is taken
        break_point: switch (msg) {  // Break point needed for MSG_BOOT to break out of case
                                     // if tree died.

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
                boolean treeUpdate = tree.update();

                // sendToView() only when tree is alive//Joseph
                showNotification(treeUpdate);

                if (treeUpdate) {
                    Log.d("ARBOR_MSG_UPDATE_NEED", "alive is true");
                    sendToView();

                pendingIntent = PendingIntent.getService(this, 0, intent, 0);

                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + (ALARM_HOUR * 1000), pendingIntent);
                    saveGame();
                }
                else {
                    Log.d("ARBOR_MSG_UPDATE_NEED", "alive is false");
                    sharedPreferences.edit().putBoolean("TREE_ALIVE", false).apply();
                    sharedPreferences.edit().putBoolean("TOGGLE", false).apply();
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
                phaseNotification();
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
                pedometer.register();

                List<Object> list = DataManager.readState(this, filename);
                loadState(list);

                startGame();

                Intent weatherIntent = new Intent(MainService.this.getApplicationContext(), MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_WEATHER_VIEW);
                PendingIntent weatherPendingIntent = PendingIntent.getService(MainService.this, 1, weatherIntent, 0);

                sendWeatherToView(weatherPendingIntent);
                Log.d("ARBOR_WEATHER", "Exiting MSG_TREE_GAME");

                break;

            // Resume game when you start activity
            case MSG_RESUME_TREE_GAME:
                startGame();

                Intent weatherIntentAgain = new Intent(MainService.this.getApplicationContext(), MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_WEATHER_VIEW);
                PendingIntent weatherPendingIntentAgain = PendingIntent.getService(MainService.this, 1, weatherIntentAgain, 0);

                sendWeatherToView(weatherPendingIntentAgain);
                Log.d("ARBOR_WEATHER", "Exiting MSG_TREE_GAME Again");

                break;

            // Store sends this message, updates the tree with the right item
            case MSG_PURCHASE:
                tree.purchase((ShopTab.StoreItem) intent.getExtras().get("STORE_ITEM"));
                sendToView();
                saveGame();
                break;

            // Update the weather and temperature fields
            case MSG_UPDATE_WEATHER_VIEW:
                Log.d("ARBOR_WEATHER", "Retrieved MSG_UPDATE_WEATHER_VIEW");

                pendingIntent = PendingIntent.getService(this.getApplicationContext(), 1, intent, 0);

                sendWeatherToView(pendingIntent);
                break;

            // User have changed settings, make the live
            case MSG_USER_INPUT:
                readUserSettings();
                pedometer.setGender(userGender);
                pedometer.setHeight(userLength);
                break;


            case MSG_BOOT:
                long now = System.currentTimeMillis();
                SharedPreferences sharedPreferences = getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE);
                long then = sharedPreferences.getLong("SHUTDOWN_TIME", now); // second argument is important
                long interval = now - then;
                Log.d("ARBOR_AGE", "interval millisec: " + interval);
                interval = interval/1000/60/60; // number of hours
                Log.d("ARBOR_AGE", "interval: " + interval);
                Log.d("ARBOR_AGE", "Inside case MSG_BOOT");

                // Do the update as many times as needed.
                for (int i = 0; i < interval; i++) {
                    boolean alive = tree.update();
                    if (!alive) {
                        // TODO: Insert this from deathOfTree when is merged
                        Log.d("ARBOR_MSG_UPDATE_NEED", "alive is false");
                        sharedPreferences.edit().putBoolean("TREE_ALIVE", false).apply();
                        sharedPreferences.edit().putBoolean("TOGGLE", false).apply();
                        // TODO: Check if it's enough to unregister or if reset() is needed as well.
                        // Pedometer will stop updating km to MainService
                        pedometer.unregister();
                        Intent intentTreeDeath = new Intent();
                        intentTreeDeath.setAction(TREE_DEAD);
                        MainService.this.sendBroadcast(intentTreeDeath);
                        break break_point;
                    }
                }

                Log.d("ARBOR_AGE", "WaterLevel: " + tree.getWaterLevel() + ", SunLevel: " + tree.getSunLevel());
                // after reboot, save new age and buffer values
                // (because otherwise MSG_TREE_GAME "overrides" these updated current values
                saveGame();
                sendToView(); // Was before saveGame();

                // Start tree update 1 hour after booting.
                Intent intentToUpdate = new Intent(this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MSG_UPDATE_NEED);
                PendingIntent pendingToUpdate = PendingIntent.getService(this, 0, intentToUpdate, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (15 * 60 * 1000), pendingToUpdate);
//                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()
//                        + (ALARM_HOUR * 1000), pendingToUpdate);
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
            if (objects.get(0) != null && objects.get(0).getClass() == Tree.class) {
                tree = (Tree) objects.get(0);
            } else {
                tree = new Tree();
                Log.e(TAG, "Tree was not found in file: " + filename + ", tree = new Tree()");
            }
            if (objects.get(2) != null  && objects.get(2).getClass() == Double.class ) { //TODO: getClass not working
                totalDistance = (double) objects.get(2);
            } else {
                totalDistance = 0;
                Log.e(TAG, "totalDistance was not found in file: " + filename + ", totalDistance = 0");
            }
            if (objects.get(3) != null  && objects.get(3).getClass() == Integer.class ) { //TODO: getClass not working
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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arbor_app_icon);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.arbor_app_icon)
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
    public void onDestroy() {
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
        intent.putExtra("AGE",
                System.currentTimeMillis() - getSharedPreferences("se.kth.projectarbor.project_arbor", MODE_PRIVATE).getLong("TREE_START_TIME", 0));
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

    private void readUserSettings() {
        if (sharedPreferences.contains("USER_GENDER")) {
            userGender = Pedometer.Gender.fromString(sharedPreferences.getString("USER_GENDER", "Female"));
        } else { Log.e(TAG, "user_gender was not found"); }
        if (sharedPreferences.contains("USER_HEIGHT")) {
            userLength = sharedPreferences.getFloat("USER_HEIGHT", 1.5f);
        } else { Log.e(TAG, "user_height was not found"); }
    }

    private void sendDistanceInfo() {
        Intent intent = new Intent();
        intent.setAction(Pedometer.DISTANCE_BROADCAST);
        intent.putExtra("DISTANCE", pedometer.getSessionDistance());
        intent.putExtra("STEPCOUNT", pedometer.getSessionStepCount());
        MainService.this.sendBroadcast(intent);

    }

    //Joseph
    // This will create a notification whenefer sun/water buffer is empty or the tree is dead
    // Checking is done every hour... see MSG_UPDATE_NEED
    private void showNotification(boolean treeUpdate) {
        Log.d("JOSEPH", "SHowNotification");
        Intent resumeIntent = new Intent(this, MainUIActivity.class);
        resumeIntent = putTreeInformation(resumeIntent);
        PendingIntent resumePending = PendingIntent.getActivity(this, 0, resumeIntent, 0);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Create a notification when WATERBUFFER is empty
        if (tree.getWaterLevel() == 0) {

            Log.d("JOSEPH", "SHowNotificationWATER");

            NotificationCompat.Builder waterBufferNotification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.health_icon)
                    .setContentTitle("Arbor")
                    .setContentText("WaterBuffer is empty!")
                    .setContentIntent(resumePending);
            mNotificationManager.notify("water", 2, waterBufferNotification.build());
        }

        //Create a notification when SUNBUFFER is empty

        if (tree.getSunLevel() == 0) {
            Log.d("JOSEPH", "SHowNotificationSUN");
            NotificationCompat.Builder sunBufferNotification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.health_icon)
                    .setContentTitle("Arbor")
                    .setContentText("SunBuffer is empty!")
                    .setContentIntent(resumePending);

            mNotificationManager.notify("sun", 3, sunBufferNotification.build());

        }

        //Create a notification when TREE IS DEAD
        if (!treeUpdate) {
            Log.d("JOSEPH", "SHowNotificationHEALTH");
            NotificationCompat.Builder treeDeadNotification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.health_icon)
                    .setContentTitle("Arbor")
                    .setContentText("Your Tree Is DEAD :( ")
                    .setContentIntent(resumePending);
            Notification n2 = treeDeadNotification.build();
            mNotificationManager.notify("tree", 4, n2);

        }

    }

    //Create a notification when Tree Phase is changed
    //Checking for phasechange will happen every km... see MSG_KM_DONE
    public void phaseNotification(){

        if (tree.phaseChanged) {
            Intent resumeIntent = new Intent(this, MainUIActivity.class);
            resumeIntent = putTreeInformation(resumeIntent);
            PendingIntent resumePending = PendingIntent.getActivity(this, 0, resumeIntent, 0);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder phaseChangedNotification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.phase_icon)
                    .setContentTitle("Arbor")
                    .setContentText("phase changed to: " +tree.getTreePhase())
                    .setContentIntent(resumePending);
            Notification n3 = phaseChangedNotification.build();
            mNotificationManager.notify("phase", 5, n3);
            tree.phaseChanged =false;
        }
    }
}


