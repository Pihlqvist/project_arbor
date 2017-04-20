package se.kth.projectarbor.project_arbor;

/**
 * Created by pethrus on 2017-04-20.
 */

public class TestTree {

    private final int SEED_WATERBUFFER_MAX = 20;
    private final int SEED_SUNBUFFER_MAX = 40;
    private final int SEED_HEALTHBUFFER_MAX = 0;


    private int age;
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

}
