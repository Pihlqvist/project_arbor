package se.kth.projectarbor.project_arbor;

/**
 * Created by pethrus and lovisa on 2017-04-20.
 *
 * The tree class is only instantiated once in the beginning of the game lifecycle.
 * The tree is a passive object. To communicate with the tree, use the interface methods below.
 */

public class TestTree {

    private final int SEED_WATERBUFFER_MAX = 20;
    private final int SEED_SUNBUFFER_MAX = 40;
    private final int SEED_HEALTHBUFFER_MAX = 0;

    private final int SPROUT_WATERBUFFER_MAX = 114;
    private final int SPROUT_SUNBUFFER_MAX = 182;
    private final int SPROUT_HEALTHBUFFER_MAX = 3;

    private final int SAPLING_WATERBUFFER_MAX = 472;
    private final int SAPLING_SUNBUFFER_MAX = 708;
    private final int SAPLING_HEALTHBUFFER_MAX = 10;

    private final int GROWN_TREE_WATERBUFFER_MAX = 950;
    private final int GROWN_TREE_SUNBUFFER_MAX = 1186;
    private final int GROWN_TREE_HEALTHBUFFER_MAX = 23;

    private Phase treePhase;
    private Buffer waterBuffer;
    private Buffer sunBuffer;
    private Buffer healthBuffer;

        private enum Phase {
            SEED, SPROUT, SAPLING, GROWN_TREE
        }

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

            public void setValue(int newValue) { this.value = newValue };

            public void incrValue(int increaseBy){
                this.value = this.value + increaseBy;
            }

            public void decrValue(int decreaseBy){
                this.value = this.value - decreaseBy;
            }
        }

    public TestTree(){
        this.age = 0;
        this.treePhase = Phase.SEED;
        this.waterBuffer = new Buffer(SEED_WATERBUFFER_MAX);
        this.sunBuffer = new Buffer(SEED_SUNBUFFER_MAX);
        this.healthBuffer = new Buffer(SEED_HEALTHBUFFER_MAX);
    }

    public int getWaterLevel(){
        return waterBuffer.value;
    }

    public int getSunLevel(){
        return sunBuffer.value;
    }

    public int getHealth(){
        return healthBuffer.value;
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
