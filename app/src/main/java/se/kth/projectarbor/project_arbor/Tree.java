package se.kth.projectarbor.project_arbor;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.Serializable;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by pethrus and lovisa on 2017-04-20.
 *
 * The tree class is only instantiated once in the beginning of the game lifecycle.
 * To communicate with the tree, use the methods below.
 */

public class Tree implements Serializable {
    // IMPORTANT for serialization, DO NOT REMOVE
    private static final long serialVersionUID = 5911304372524803500L;
    private SharedPreferences sharedPreferences;

    //Seed phase constants
    private final int SEED_WATERBUFFER_MAX = 20;
    private final int SEED_SUNBUFFER_MAX = 40;
    private final int SEED_HEALTHBUFFER_MAX = 1;
    private final int SEED_WATER_NEED = 1; // need per hour
    private final int SEED_SUN_NEED = 2; // need per hour
    private final int SEED_WATER_INTAKE = 7; // intake per kilometer
    private final int SEED_SUN_INTAKE = 14; // intake per kilometer
    private final int SEED_NEXT_PHASE = 7; // number of km where SEED goes into next phase

    //Sprout phase constants
    private final int SPROUT_WATERBUFFER_MAX = 114;
    private final int SPROUT_SUNBUFFER_MAX = 182;
    private final int SPROUT_HEALTHBUFFER_MAX = 3;
    private final int SPROUT_WATER_NEED = 5; // need per hour
    private final int SPROUT_SUN_NEED = 8; // need per hour
    private final int SPROUT_WATER_INTAKE = 34; // intake per kilometer
    private final int SPROUT_SUN_INTAKE = 55; // intake per kilometer
    private final int SPROUT_NEXT_PHASE = 20; // number of km where SPROUT goes into next phase

    //Sapling phase constants
    private final int SAPLING_WATERBUFFER_MAX = 472;
    private final int SAPLING_SUNBUFFER_MAX = 708;
    private final int SAPLING_HEALTHBUFFER_MAX = 10;
    private final int SAPLING_WATER_NEED = 20; // need per hour
    private final int SAPLING_SUN_NEED = 30; // need per hour
    private final int SAPLING_WATER_INTAKE = 137; // intake per kilometer
    private final int SAPLING_SUN_INTAKE = 206; // intake per kilometer
    private final int SAPLING_NEXT_PHASE = 50; // number of km where SAPLING goes into next phase

    //Grown tree phase constants
    private final int GROWN_TREE_WATERBUFFER_MAX = 950;
    private final int GROWN_TREE_SUNBUFFER_MAX = 1186;
    private final int GROWN_TREE_HEALTHBUFFER_MAX = 23;
    private final int GROWN_TREE_WATER_NEED = 40; // need per hour
    private final int GROWN_TREE_SUN_NEED = 50; // need per hour
    private final int GROWN_TREE_WATER_INTAKE = 274; // intake per kilometer
    private final int GROWN_TREE_SUN_INTAKE = 343; // intake per kilometer

    //Tree attributes
    private Phase treePhase;
    private Buffer waterBuffer;
    private Buffer sunBuffer;
    private Buffer healthBuffer;
    private boolean alive;

    // Control variables
    private int time;
    private int dist;
    private boolean timerFlag;

    public enum Phase {
        SEED(1), SPROUT(2), SAPLING(3), GROWN_TREE(4);

        private int phaseNumber;

        private Phase(int phaseNumber) {
            this.phaseNumber = phaseNumber;
        }

        public int getPhaseNumber() {
            return phaseNumber;
        }

    }

    //The buffer class keeps track of the buffer value that is an integer
    // between 0 and a max value that is dependent on the tree phase
    private class Buffer implements Serializable {
        // IMPORTANT for serialization, DO NOT REMOVE
        private static final long serialVersionUID = 8087200635129916880L;

        private int max;
        private int value;

        public Buffer(int max){
            this.max = max;
            this.value = max;
        }

        public void setMax(int newMax) {
            this.max = newMax;
        }

        public void setValue(int newValue) { this.value = newValue; };

        public int getValue(){
            return value;
        }
        //increases the buffer value, if the buffer is full the value is set to max
        public void incrValue(int increaseBy){
            if((value + increaseBy) < max)
                value += increaseBy;
            else
                value = max;
        }
        //decreases the buffer value, if the buffer is empty the value is set to zero
        public void decrValue(int decreaseBy){
            if((value - decreaseBy) > 0)
                value -= decreaseBy;
            else
                value = 0;
        }
    }

    public Tree(){
        this.treePhase = Phase.SEED;
        this.waterBuffer = new Buffer(SEED_WATERBUFFER_MAX);
        this.sunBuffer = new Buffer(SEED_SUNBUFFER_MAX);
        this.healthBuffer = new Buffer(SEED_HEALTHBUFFER_MAX);
        this.time = 0;
        this.timerFlag = false;
        this.alive = true;
        this.dist = 0;
    }

    public int getWaterLevel(){
        return waterBuffer.getValue();
    }

    public int getSunLevel(){
        return sunBuffer.getValue();
    }

    public int getHealth(){
        return healthBuffer.getValue();
    }

    public int getWaterBufferMax(){
        return waterBuffer.max;
    }

    public int getSunBufferMax(){
        return sunBuffer.max;
    }

    public int getHealthBufferMax(){
        return healthBuffer.max;
    }

    public Phase getTreePhase(){
        return treePhase;
    }

    //This method is called when it's time for the tree object to change phase
    //It changes the tree objects attributes to match the current phase
    //and fills all the buffers to max
    public void changePhase(){
        switch(this.treePhase) {
            case SEED:
                this.treePhase = Phase.SPROUT;

                this.waterBuffer.setMax(SPROUT_WATERBUFFER_MAX);
                this.waterBuffer.setValue(SPROUT_WATERBUFFER_MAX);
                this.sunBuffer.setMax(SPROUT_SUNBUFFER_MAX);
                this.sunBuffer.setValue(SPROUT_SUNBUFFER_MAX);
                this.healthBuffer.setMax(SPROUT_HEALTHBUFFER_MAX);
                this.healthBuffer.setValue(SPROUT_HEALTHBUFFER_MAX);

                break;
            case SPROUT:
                this.treePhase = Phase.SAPLING;

                this.waterBuffer.setMax(SAPLING_WATERBUFFER_MAX);
                this.waterBuffer.setValue(SAPLING_WATERBUFFER_MAX);
                this.sunBuffer.setMax(SAPLING_SUNBUFFER_MAX);
                this.sunBuffer.setValue(SAPLING_SUNBUFFER_MAX);
                this.healthBuffer.setMax(SAPLING_HEALTHBUFFER_MAX);
                this.healthBuffer.setValue(SAPLING_HEALTHBUFFER_MAX);
                break;
            case SAPLING:
                this.treePhase = Phase.GROWN_TREE;

                this.waterBuffer.setMax(GROWN_TREE_WATERBUFFER_MAX);
                this.waterBuffer.setValue(GROWN_TREE_WATERBUFFER_MAX);
                this.sunBuffer.setMax(GROWN_TREE_SUNBUFFER_MAX);
                this.sunBuffer.setValue(GROWN_TREE_SUNBUFFER_MAX);
                this.healthBuffer.setMax(GROWN_TREE_HEALTHBUFFER_MAX);
                this.healthBuffer.setValue(GROWN_TREE_HEALTHBUFFER_MAX);
                break;
        }
    }
    // update() is used to decrease water/sun buffers every hour. If at least one buffer reaches
    // is 0, 1 HP is withdrawn and a "timer" is started and a timerFlag set to true.
    // Until timer reaches 24, timerFlag will be true and block withdrawing of HP:s. When timerFlag
    // is put to false after 24 hours and both buffers are not 0, 1 HP is added to HPBuffer.
    public boolean update() {
        bufferDecrease();
        // timerFlag is true so HP cannot be decreased during this time.

        if (timerFlag) {
            time += 1;
        } else {
            if (this.sunBuffer.value <= 0) {
                timerFlag = true;
                healthChange(-1);
            }
            if (this.waterBuffer.value <= 0) {
                timerFlag = true;
                healthChange(-1);
            }
        }
        // timerFlag is turned to false and HP ma be added if buffers are not 0.
        if (time == 24) {
            time = 0;
            timerFlag = false;
            System.out.println(timerFlag);
            if (getWaterLevel() > 0 && getSunLevel() > 0 && this.getHealth() < this.getHealthBufferMax()) {
                healthChange(1);
            }
        }
        return alive;
    }
    // bufferDecrease() decreases both waterbuffer and sunbuffer when called by mainservice every hour
    // If one or both buffers reaches 0, it will also call on healthChange() to decrease HP.
    public void bufferDecrease(){
        switch(this.treePhase){
            case SEED:
                waterBuffer.decrValue(SEED_WATER_NEED);
                sunBuffer.decrValue(SEED_SUN_NEED);
                break;
            case SPROUT:
                waterBuffer.decrValue(SPROUT_WATER_NEED);
                sunBuffer.decrValue(SPROUT_SUN_NEED);
                break;
            case SAPLING:
                waterBuffer.decrValue(SAPLING_WATER_NEED);
                sunBuffer.decrValue(SAPLING_SUN_NEED);
                break;
            case GROWN_TREE:
                waterBuffer.decrValue(GROWN_TREE_WATER_NEED);
                sunBuffer.decrValue(GROWN_TREE_SUN_NEED);
                break;
        }
    }
    // bufferIncrease() is called from MainService every accomplished kilometer
    public void bufferIncrease(Environment.Weather weather) {
        // Update phase if needed.
        dist++;
        switch(dist) {
            case SEED_NEXT_PHASE:
                this.changePhase();
                break;
            case SPROUT_NEXT_PHASE:
                this.changePhase();
                break;
            case SAPLING_NEXT_PHASE:
                this.changePhase();
                break;
        }
        // Add fixed amount of resources depending on weather and phase.
        switch(weather) {
            case SUN:
                addSunIntake();
                if (getSunLevel() > getSunBufferMax())
                    sunBuffer.setValue(getSunBufferMax());
                break;
            case RAIN:
                addWaterIntake();
                if (getWaterLevel() > getWaterBufferMax())
                    waterBuffer.setValue(getWaterBufferMax());
                break;
            default: // if neither sun nor rain, cloudy
                break;
        }

    }
        // Add sun amount depending on phase.
        private void addSunIntake(){
            Phase phase = getTreePhase();
            switch(phase) {
                case SEED:
                    sunBuffer.value += SEED_SUN_INTAKE;
                    break;
                case SPROUT:
                    sunBuffer.value += SPROUT_SUN_INTAKE;
                    break;
                case SAPLING:
                    sunBuffer.value += SAPLING_SUN_INTAKE;
                    break;
                case GROWN_TREE:
                    sunBuffer.value += GROWN_TREE_SUN_INTAKE;
                    break;
            }
        }

        // Add water amount per km depending on phase.
        private void addWaterIntake(){
            Phase phase = getTreePhase();
            switch(phase) {
                case SEED:
                    waterBuffer.value += SEED_WATER_INTAKE;
                    break;
                case SPROUT:
                    waterBuffer.value += SPROUT_WATER_INTAKE;
                    break;
                case SAPLING:
                    waterBuffer.value += SAPLING_WATER_INTAKE;
                    break;
                case GROWN_TREE:
                    waterBuffer.value += GROWN_TREE_WATER_INTAKE;
                    break;
            }
        }
            // Increases or decreases tree health and sets boolean alive to false if tree dies.
            // Used by update()
            private void healthChange(int valueOfChange){
                if(valueOfChange < 0)
                    this.healthBuffer.decrValue(-valueOfChange);
                else
                    this.healthBuffer.incrValue(valueOfChange);

                if(getHealth() <= 0) {
                    alive = false;
                    this.healthBuffer.value = 0;
                }
            }
    // Used by main service when buying water in store
    public void changeWaterBuffer(boolean increase, int amount){
        if (increase)
            waterBuffer.incrValue(amount);
        else
            waterBuffer.decrValue(amount);
    }
    // Used by main service when buying sun in store
    public void changeSunBuffer(boolean increase, int amount){
        if (increase)
            sunBuffer.incrValue(amount);
        else
            sunBuffer.decrValue(amount);
    }
    // Not used yet.
    public void changeHealthBuffer(boolean increase, int amount){
        if (!timerFlag) {
            if (increase)
                healthBuffer.incrValue(amount);
            else
                healthBuffer.decrValue(amount);
        }
    }


    public void purchase(ShopTab.StoreItem storeItem) {

        switch (storeItem) {

            case SUN:
                Log.d("ARBOR_TREE", "purchase sun");
                sunBuffer.incrValue(storeItem.getAmount());
                break;

            case WATER:
                Log.d("ARBOR_TREE", "purchase water");
                waterBuffer.incrValue(storeItem.getAmount());
                break;

        }


    }



}
