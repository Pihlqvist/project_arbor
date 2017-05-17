package se.kth.projectarbor.project_arbor;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Ramcin on 2017-04-27. Modified by Fredrik Pihlqvist
 *
 * Keeps track of the users steps and handles it, broadcasts the information to
 * the activity that wants it.
 */

class Pedometer {

    public enum Gender {
        MALE(0.415, "Male"),
        NON_BINARY(0.414, "Non-binary"),
        FEMALE(0.413, "Female");

        private final double multiplicativeFactor;
        private final String gender;


        Gender(double multiplicativeFactor, String gender) {
            this.multiplicativeFactor = multiplicativeFactor;
            this.gender = gender;
        }

        public double getMultiplicativeFactor() {
            return multiplicativeFactor;
        }

        public static Gender fromString(String gender) {
            for (Gender g : Gender.values()) {
                if (g.gender.equals(gender)) {
                    return g;
                }
            }
            return null;
        }
    }

    private final static String TAG = "ARBOR_PEDOMETER";
    public final static String DISTANCE_BROADCAST = "se.kth.projectarbor.project_arbor.intent.DISTANCE";
    public final static String STORE_BROADCAST = "se.kth.projectarbor.project_arbor.intent.STORE";
    private final static int BUFFER_CONSTANT = 1000;

    private double height;
    private Gender gender;
    private int currentStepCount = 0;
    private int stepCount = 0;
    private int totalStepCount;
    private int referenceStepCount = -1;
    private double distance;
    private double totalDistance;
    private double updateDistance;
    private int phaseNumber;

    private SensorManager sensorManager;
    private Sensor stepCounter;
    private SensorEventListener listener;
    private Context context;
    private Intent broadcast;
    private Intent storeBroadcast;
    private double coefficient;
    private int updateOn = 10;
    private boolean registered = false;

    public Pedometer(Context context, double height, Gender gender) {
        this(context, height, gender, 0, 0, 1);
    }

    public Pedometer(Context context, double height, Gender gender, double totalDistance, int totalStepCount, int phaseNumber) {
        this.context = context;
        this.height = height;
        this.gender = gender;
        this.totalDistance = totalDistance;
        this.totalStepCount = totalStepCount;
        this.coefficient = height * gender.getMultiplicativeFactor();
        this.phaseNumber = phaseNumber;

        updateDistance =  totalDistance % BUFFER_CONSTANT;

        this.broadcast = new Intent();
        broadcast.setAction(DISTANCE_BROADCAST);
        this.storeBroadcast = new Intent();
        storeBroadcast.setAction(STORE_BROADCAST);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        listener = new PedometerEventListener();
        // sensorManager.registerListener(listener, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void register() {
        Log.d(TAG, "register()");
        registered = true;
        sensorManager.registerListener(listener, stepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister() {
        Log.d(TAG, "unregister()");
        registered = false;
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

    public int getTotalStepCount() {
        return totalStepCount;
    }

    public double getSessionDistance() {
        return distance;
    }

    public int getSessionStepCount() {
        return stepCount;
    }

    public void setTotalStepCount(int totalStepCount) {
        this.totalStepCount = totalStepCount;
    }

    public void setPhaseNumber(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setHeight(Double height) {
        this.height = height;
    }
    
    public boolean isRegisterd() {
        return registered;
    }

    void setGender(String gender){
        switch (gender){
            case "Female":
                this.gender = Gender.FEMALE;
                break;
            case "Male":
                this.gender = Gender.MALE;
                break;
            case "Non-binary":
                this.gender = Gender.NON_BINARY;
                break;
        }
    }

    void setHeight(float height){
        this.height = (double) height;
    }

    public void reset() {
        currentStepCount = 0;
        stepCount = 0;
        referenceStepCount = -1;
        distance = 0;
    }

    public void resetAll() {
        currentStepCount = 0;
        stepCount = 0;
        referenceStepCount = -1;
        distance = 0;
        totalDistance = 0;
        totalStepCount = 0;
    }

    public void resetAndRegister() {
        reset();
        register();
    }

    private class PedometerEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                int value = Math.round(event.values[0]);
                if (referenceStepCount < 0) {
                    referenceStepCount = value;
                }
                currentStepCount = value - referenceStepCount;
                distance += coefficient * currentStepCount;
                totalStepCount += currentStepCount;
                totalDistance += coefficient*currentStepCount;

                // TODO: Recently implemented, be wary of bugs!
                updateDistance += coefficient * currentStepCount;
                if (updateDistance >= BUFFER_CONSTANT) {
                    updateDistance -= BUFFER_CONSTANT;
                    context.startService(new Intent(context, MainService.class)
                            .putExtra("MESSAGE_TYPE", MainService.MSG_KM_DONE));

                    // TODO: give proper amount
                    storeBroadcast.putExtra("MONEY", phaseNumber);
                    context.getApplicationContext().sendBroadcast(storeBroadcast);
                }
                stepCount += currentStepCount;
                referenceStepCount = value;
                updateOn += currentStepCount;
                //updateOn tries to capture every 10 step and do the broadcast when captured.
                if (updateOn >= 10) {
                    broadcast.putExtra("DISTANCE", distance);
                    broadcast.putExtra("TOTALDISTANCE", totalDistance); // StatsTab listens to this
                    broadcast.putExtra("TOTALSTEPCOUNT", totalStepCount); // StatsTab listens to this
                    broadcast.putExtra("STEPCOUNT", stepCount);
                    context.getApplicationContext().sendBroadcast(broadcast);
                    //TODO: Make it better ie. istead of subtraction
                    updateOn -= 10;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
