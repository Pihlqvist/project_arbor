package se.kth.projectarbor.project_arbor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MainService extends Service {
    private final IBinder binder = new ServiceBinder;

    public class ServiceBinder extends Binder {
        //Provides Service bind methods

        //getServiceInstance provides a instance of the Service
        MainService getServiceInstance() {
            return MainService.this;
        }
    }
    public MainService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }
    }
}
