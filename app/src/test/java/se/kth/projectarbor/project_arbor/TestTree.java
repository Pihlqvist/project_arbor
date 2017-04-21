package se.kth.projectarbor.project_arbor;

/**
 * Created by pethrus and lovisa on 2017-04-20.
 *
 * The tree class is only instantiated once in the beginning of the game lifecycle.
 * The tree is a passive object. To communicate with the tree, use the interface methods below.
 */

public class TestTree {

    //Seed phase constants
    private final int SEED_WATERBUFFER_MAX = 20;
    private final int SEED_SUNBUFFER_MAX = 40;
    private final int SEED_HEALTHBUFFER_MAX = 0;

    //Sprout phase constants
    private final int SPROUT_WATERBUFFER_MAX = 114;
    private final int SPROUT_SUNBUFFER_MAX = 182;
    private final int SPROUT_HEALTHBUFFER_MAX = 3;

    //Spaling phase constants
    private final int SAPLING_WATERBUFFER_MAX = 472;
    private final int SAPLING_SUNBUFFER_MAX = 708;
    private final int SAPLING_HEALTHBUFFER_MAX = 10;

    //Grown tree phase constants
    private final int GROWN_TREE_WATERBUFFER_MAX = 950;
    private final int GROWN_TREE_SUNBUFFER_MAX = 1186;
    private final int GROWN_TREE_HEALTHBUFFER_MAX = 23;

    //Tree attributes
    private Phase treePhase;
    private Buffer waterBuffer;
    private Buffer sunBuffer;
    private Buffer healthBuffer;

    private enum Phase {
        SEED, SPROUT, SAPLING, GROWN_TREE
    }

        //The buffer class keeps track of the buffer value that is an integer
        // between 0 and a max value that is dependent on the tree phase
        private class Buffer {
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

    public TestTree(){
        this.treePhase = Phase.SEED;
        this.waterBuffer = new Buffer(SEED_WATERBUFFER_MAX);
        this.sunBuffer = new Buffer(SEED_SUNBUFFER_MAX);
        this.healthBuffer = new Buffer(SEED_HEALTHBUFFER_MAX);
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


    //To increase a buffer with the amount given in the amount argument
    // the boolean arguments should be set to true, to decrease it should be false
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
