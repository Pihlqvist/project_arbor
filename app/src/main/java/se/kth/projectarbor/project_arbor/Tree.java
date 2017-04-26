package se.kth.projectarbor.project_arbor;

import java.io.Serializable;

/**
 * Created by pethrus and lovisa on 2017-04-20.
 *
 * The tree class is only instantiated once in the beginning of the game lifecycle.
 * The tree is a passive object. To communicate with the tree, use the interface methods below.
 */

public class Tree implements Serializable {
    // IMPORTANT for serialization, DO NOT REMOVE
    private static final long serialVersionUID = 5911304372524803500L;

    //Seed phase constants
    private final int SEED_WATERBUFFER_MAX = 20;
    private final int SEED_SUNBUFFER_MAX = 40;
    private final int SEED_HEALTHBUFFER_MAX = 1;
    private final int SEED_WATER_NEED = 1; // need per hour
    private final int SEED_SUN_NEED = 2; // need per hour

    //Sprout phase constants
    private final int SPROUT_WATERBUFFER_MAX = 114;
    private final int SPROUT_SUNBUFFER_MAX = 182;
    private final int SPROUT_HEALTHBUFFER_MAX = 3;
    private final int SPROUT_WATER_NEED = 5; // need per hour
    private final int SPROUT_SUN_NEED = 8; // need per hour

    //Sapling phase constants
    private final int SAPLING_WATERBUFFER_MAX = 472;
    private final int SAPLING_SUNBUFFER_MAX = 708;
    private final int SAPLING_HEALTHBUFFER_MAX = 10;
    private final int SAPLING_WATER_NEED = 20; // need per hour
    private final int SAPLING_SUN_NEED = 30; // need per hour

    //Grown tree phase constants
    private final int GROWN_TREE_WATERBUFFER_MAX = 950;
    private final int GROWN_TREE_SUNBUFFER_MAX = 1186;
    private final int GROWN_TREE_HEALTHBUFFER_MAX = 23;
    private final int GROWN_TREE_WATER_NEED = 40; // need per hour
    private final int GROWN_TREE_SUN_NEED = 50; // need per hour

    //Tree attributes
    private Phase treePhase;
    private Buffer waterBuffer;
    private Buffer sunBuffer;
    private Buffer healthBuffer;

    // Control variables
    private int time;

    public enum Phase {
        SEED, SPROUT, SAPLING, GROWN_TREE
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
            if(value + increaseBy < max)
                value += increaseBy;
            else
                value = max;
        }

        //decreases the buffer value, if the buffer is empty the value is set to zero
        public void decrValue(int decreaseBy){
            if(value - decreaseBy > 0)
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
    }

    //Returns the water buffer status
    public int getWaterLevel(){
        return waterBuffer.getValue();
    }

    //Returns the sun buffer status
    public int getSunLevel(){
        return sunBuffer.getValue();
    }

    //Returns the health status
    public int getHealth(){
        return healthBuffer.getValue();
    }

    public Phase getTreePhase(){
        return treePhase;
    }

    // This method decreases both waterbuffer and sunbuffer when called by mainservice
    public void bufferDecrese(){
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
        // when buffers is 0 health should be decreased by 1 or 2 HP
        if((this.waterBuffer.value <= 0) && (this.sunBuffer.value <= 0) ){
            waterBuffer.setValue(0);
            sunBuffer.setValue(0);
            healthChange(-2);
        }
        else if(this.sunBuffer.value <= 0){
            sunBuffer.setValue(0);
            healthChange(-1);
        }
        else if(this.waterBuffer.value <= 0){
            waterBuffer.setValue(0);
            healthChange(-1);
        }
        else {
            healthChange(1);
        }
    }

    private void healthChange(int valueOfChange){
        if(this.time == 0) {
            this.healthBuffer.value = this.healthBuffer.value + valueOfChange;
            this.time++;
        }
        else if(this.time == 23){
            this.time = 0;
        }
        else{
            this.time++;
        }


    }

    public void changeWaterBuffer(boolean increase, int amount){
        if (increase)
            waterBuffer.incrValue(amount);
        else
            waterBuffer.decrValue(amount);
    }

    public void changeSunBuffer(boolean increase, int amount){
        if (increase)
            sunBuffer.incrValue(amount);
        else
            sunBuffer.decrValue(amount);
    }

    public void changeHealthBuffer(boolean increase, int amount){
        if (increase)
            healthBuffer.incrValue(amount);
        else
            healthBuffer.decrValue(amount);
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
}
