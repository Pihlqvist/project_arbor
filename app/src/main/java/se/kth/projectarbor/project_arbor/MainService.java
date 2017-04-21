package se.kth.projectarbor.project_arbor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

public class MainService extends Service {
    private ServiceHandler handler;
    private LocationManager locationManager;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null) return;
            switch (msg.what) {
                case LocationManager.MSG_START_RUN:
                    Toast.makeText(MainService.this,
                            "Service is tracking your activity", Toast.LENGTH_SHORT).show();
                    locationManager.connect();
                    break;
                case LocationManager.MSG_STOP_RUN:
                    locationManager.disconnect();
                    Toast.makeText(MainService.this,
                            "Service has stopped tracking your activity", Toast.LENGTH_SHORT).show();
                    break;
                default:
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
        HandlerThread thread = new HandlerThread("StartedService", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        Looper looper = thread.getLooper();
        handler = new ServiceHandler(looper);

        locationManager = new LocationManager(this, 10000, 5);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {
            Message msg = handler.obtainMessage();
            msg.arg1 = startId;
            msg.what = intent.getExtras().getInt("MESSAGE_TYPE");
            handler.sendMessage(msg);
        }

        // return START_REDELIVER_INTENT;
        return START_STICKY;
    }
}

