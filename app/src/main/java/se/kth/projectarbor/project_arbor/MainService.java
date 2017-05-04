package se.kth.projectarbor.project_arbor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
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
    public final static String WEATHER_DATA = "se.kth.projectarbor.project_arbor.intent.WEATHER_DATA";

    private final static String TAG = "ARBOR_SERVICE";
    final static String filename = "user42.dat";

    // Times in seconds that the alarm will take to repeat the service
    public final static int ALARM_HOUR = 14;  // TODO: changed to min for testing
    public final static int ALARM_DAY = 24 * 60 * 60;

    // Messages to be used in Service. Don't use 0, it will mess up everything
    public final static int MSG_START = 1;
    public final static int MSG_STOP = 2;
    public final static int MSG_UPDATE_NEED = 3;

    public final static int MSG_KM_DONE = 5;
    public final static int MSG_UPDATE_VIEW = 6;
    public final static int MSG_TREE_GAME = 7;
    public final static int MSG_PURCHASE = 8;
    public final static int MSG_UPDATE_WEATHER_VIEW = 9;

    public final static int MAIN_FOREGROUND = 111;

    // MainService works with following components
    private Pedometer pedometer;
    private Tree tree;
    private Environment environment;
    private AlarmManager alarmManager;
    private double totalDistance;
    private Environment.Weather lastWeather;
    private double lastTemperature;
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
        pedometer = new Pedometer(getApplicationContext(), userLength, userGender, totalDistance
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

        final PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);


        // Depending on the msg a different action is taken
        switch (msg) {

            // Start pedometer and start a foreground
            case MSG_START:
                pedometer.resetAndRegister();
                // TODO: Do we need to read here ?
                List<Object> list = DataManager.readState(this, filename);
                loadState(list);
                startForeground();
                break;

            // Stop pedometer and stop the foreground
            case MSG_STOP:
                pedometer.unregister();
                stopForeground(true);
                saveGame();

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

                Intent weatherIntent = new Intent(MainService.this, MainService.class)
                        .putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_WEATHER_VIEW);
                //intent.removeExtra("MESSAGE_TYPE");
                //intent.putExtra("MESSAGE_TYPE", MainService.MSG_UPDATE_WEATHER_VIEW);
                PendingIntent weatherPendingIntent = PendingIntent.getService(MainService.this, 0, weatherIntent, 0);
                sendWeatherToView(weatherPendingIntent);

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
                sendWeatherToView(pendingIntent);
                break;
        }

        return START_NOT_STICKY;
    }

    // Load tree and tDistance from IO
    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        totalDistance = (Double) objects.get(2);
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
    private void sendToView() {
        Log.d(TAG, "sendToView()");
        Intent intent = new Intent();

        intent.putExtra("SUN", tree.getSunLevel());
        intent.putExtra("WATER", tree.getWaterLevel());
        intent.putExtra("HP", tree.getHealth());
        intent.putExtra("PHASE", tree.getTreePhase().toString());

        intent.setAction(TREE_DATA);
        getApplicationContext().sendBroadcast(intent);
    }

    // Start MainActivity and give it the information it needs via an intent
    private void startGame() {
        Intent intentToActivity = new Intent(this, MainUIActivity.class);
        intentToActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intentToActivity.putExtra("SUN", tree.getSunLevel());
        intentToActivity.putExtra("WATER", tree.getWaterLevel());
        intentToActivity.putExtra("HP", tree.getHealth());
        intentToActivity.putExtra("PHASE", tree.getTreePhase().toString());

        startActivity(intentToActivity);
    }


    // Save everything, this is so that we save essential information when the service dies
    private void saveGame() {
        DataManager.saveState(this, filename, tree,
                environment.getForecasts(), pedometer.getTotalDistance());
    }

    private class AsyncTaskRunner extends AsyncTask<PendingIntent, Void, PendingIntent> {

        @Override
        protected PendingIntent doInBackground(PendingIntent... params) {
            lastWeather = environment.getWeather();
            lastTemperature = environment.getTemp();

            Intent intent = new Intent();
            intent.putExtra("WEATHER", lastWeather.toString());
            intent.putExtra("TEMP", lastTemperature);
            intent.setAction(WEATHER_DATA);
            getApplicationContext().sendBroadcast(intent);

            return params[0];
        }


        @Override
        protected void onPostExecute(PendingIntent pendingIntent) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (1000), pendingIntent); // TODO: ALARM_HOUR/4 *
            Log.d("ARBOR_WEATHER", "Exiting this thread");
        }
    }

    private void sendWeatherToView(final PendingIntent pendingIntent) {
        new AsyncTaskRunner().execute(pendingIntent);
    }
}


