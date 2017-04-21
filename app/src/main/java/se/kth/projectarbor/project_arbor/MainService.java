package se.kth.projectarbor.project_arbor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {
    // Don't use 0, it will mess up everything
    final static int MSG_START = 1;
    final static int MSG_STOP = 2;
    final static int MSG_CREATE = 3;

    private final String filename = "user.dat";
    private HandlerThread thread;
    private ServiceHandler handler;

    // MainService works with following components
    private LocationManager locationManager;
    private Tree tree;
    // end

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    List<Object> objects = readState(filename);
                    loadState(objects);
                    Log.d("RAMCIN", "Service is tracking your activity and state has been read");
                    locationManager.connect();
                    break;
                case MSG_STOP:
                    locationManager.disconnect();
                    // We must add all the game components that have to be saved
                    saveState(filename, tree, locationManager.getTotalDistance());
                    Log.d("RAMCIN", "Service has stopped tracking your activity and state is saved");
                    break;
                case MSG_CREATE:
                    createUser();
                    Log.d("RAMCIN", "User created");
                    break;
                default:
                    Log.d("RAMCIN", "default");
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("RAMCIN", "OnCreate()");

        thread = new HandlerThread("StartedService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        handler = new ServiceHandler(looper);

        locationManager = new LocationManager(this, 10000, 5);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RAMCIN", "onStartCommand()");

        if (intent != null && intent.getExtras() != null) {
            Message msg = handler.obtainMessage();
            msg.arg1 = startId;
            msg.what = intent.getExtras().getInt("MESSAGE_TYPE");
            handler.sendMessage(msg);
        }

        return START_STICKY;
    }

    protected void saveState(String filename, Serializable... objects) {
        Log.d("RAMCIN", "saveState: TreePhase==" + tree.getTreePhase() + ", TotalDistance==" + locationManager.getTotalDistance());

        FileOutputStream fileOutputStream;
        ObjectOutputStream objectOutputStream;

        try {
            fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            for (Serializable o : objects) {
                // OBS! Order matters for writing and reading
                objectOutputStream.writeObject(o);
            }

            // OBS! OBS! null is written to mark the end-of-file
            objectOutputStream.writeObject(null);

            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected List<Object> readState(String filename) {
        FileInputStream fileInputStream;
        ObjectInputStream objectInputStream;
        List<Object> objects = null;

        try {
            fileInputStream = openFileInput(filename);
            objectInputStream = new ObjectInputStream(fileInputStream);
            objects = new ArrayList<>();
            Object o;

            // OBS! OBS! null is the end-of-file marker; it was written for this purpose
            while ((o = objectInputStream.readObject()) != null) {
                // OBS! Order matters for writing and reading
                objects.add(o);
            }

            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects;
    }

    private void loadState(List<Object> objects) {
        tree = (Tree) objects.get(0);
        locationManager.setTotalDistance((Float) objects.get(1));

        Log.d("RAMCIN", "loadState: TreePhase==" + tree.getTreePhase() + ", TotalDistance==" + locationManager.getTotalDistance());
    }

    // Create and save game components
    protected void createUser() {
        tree = new Tree();

        // IMPORTANT: ORDER MATTERS
        saveState(filename, tree, new Float(0));
    }
}

