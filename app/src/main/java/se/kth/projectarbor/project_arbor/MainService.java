package se.kth.projectarbor.project_arbor;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

public class MainService extends Service {

    //Binder Service

    private final IBinder binder = new ServiceBinder;

    public class ServiceBinder extends Binder {
        //Provides Service bind methods

        //getServiceInstance provides a instance of the Service
        MainService getServiceInstance() {
            return MainService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // HandlerService

    static final int MSG_START_RUN = 0;

    private final class ServiceHandler extends Handler {

        @Override
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_RUN:
                    Toast.makeText(getApplicationContext(), "Service is tracking your activity", Toast.LENGTH_SHORT).show();
                default:
                    super.handleMessage(msg);
            }
        }

    }
    private ServiceHandler handler;

    @Override
    public void onCreate(){
        Toast.makeText(getApplicationContext(), "Service is starting", Toast.LENGTH_SHORT).show();

        HandlerThread thread = new HandlerThread("StartedService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        handler = new ServiceHandler(looper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Message msg = handler.obtainMessage();
        msg.arg1 = startId;
        msg.what = intent.getExtras().getInt("MESSAGE_TYPE");

        handler.sendMessage(msg);
        return START_STICKY;
    }
    //MainService

    public MainService() {
    }
}
