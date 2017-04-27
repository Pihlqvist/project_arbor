package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Ramcin on 2017-04-27.
 */

class Pedometer {
    public enum Gender {
        MALE(0.415),
        FEMALE(0.413);

        private final double multiplicativeFactor;

        Gender(double multiplicativeFactor) {
            this.multiplicativeFactor = multiplicativeFactor;
        }

        public double getMultiplicativeFactor() {
            return multiplicativeFactor;
        }
    }

    public final static String DISTANCE_BROADCAST = "se.kth.projectarbor.project_arbor.intent.DISTANCE";
    private final static int BUFFER_CONSTANT = 1000;

    private double height;
    private Gender gender;
    private int currentStepCount = 0;
    private int stepCount = 0;
    private int referenceStepCount = -1;
    private double distance;
    private double totalDistance;
    private double updateDistance;

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private SensorEventListener listener;
    private Context context;
    private Intent broadcast;
    private double coefficient;
    private int updateOn = 10;

    public Pedometer(Context context, double height, Gender gender) {
        this(context, height, gender, 0);
    }

    public Pedometer(Context context, double height, Gender gender, double totalDistance) {
        this.context = context;
        this.height = height;
        this.gender = gender;
        this.totalDistance = totalDistance;
        this.coefficient = height * gender.getMultiplicativeFactor();

        updateDistance =  totalDistance % BUFFER_CONSTANT;

        this.broadcast = new Intent();
        broadcast.setAction(DISTANCE_BROADCAST);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        listener = new PedometerEventListener();
        // sensorManager.registerListener(listener, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void register() {
        sensorManager.registerListener(listener, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister() {
        sensorManager.unregisterListener(listener);
    }

    public double getDistance() {
        return distance;
    }

    public int getStepCount() {
        return stepCount;
    }

    public Gender getGender() {
        return gender;
    }

    public double getHeight() {
        return height;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void reset() {
        currentStepCount = 0;
        stepCount = 0;
        referenceStepCount = -1;
        distance = 0;
    }

    public void resetAndRegister() {
        reset();
        register();
    }

    private class PedometerEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                int value = (int) event.values[0];

                if (referenceStepCount < 0) {
                    referenceStepCount = value;
                }

                currentStepCount = value - referenceStepCount;
                distance += coefficient * currentStepCount;

                totalDistance += distance;

                // TODO: Recently implemented, be wary of bugs!
                updateDistance += distance;
                if (updateDistance >= BUFFER_CONSTANT) {
                    updateDistance -= BUFFER_CONSTANT;
                    context.startService(new Intent(context, MainService.class)
                            .putExtra("MESSAGE_TYPE", MainService.MSG_KM_DONE));
                }

                stepCount += currentStepCount;
                referenceStepCount = value;

                Log.d("ARBOR_PEDOMETER", stepCount + "");
                Log.d("ARBOR_PEDOMETER", distance + "");

                if (++updateOn >= 10) {
                    // Intent broadcast = new Intent();
                    // broadcast.setAction("se.kth.projectarbor.project_arbor.intent.DISTANCE");
                    Log.d("ARBOR_PEDOMETER", "sent intent");
                    broadcast.putExtra("DISTANCE", distance);
                    context.getApplicationContext().sendBroadcast(broadcast);
                    updateOn = 0;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
