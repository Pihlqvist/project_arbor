package se.kth.projectarbor.project_arbor;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {
    @Test
    public void testNeedsDecr_HealthDecr() {
        Tree tree = new Tree();
        tree.changePhase();
        for(int i = 0; i < 35; i++) {
            System.out.println("Health: " + tree.getHealth());
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            System.out.println();
            tree.update();
        }
    }

    @Test
    public void testNeedsDecr_NeedsIncr_HealthDecr(){
        Tree tree = new Tree();
        tree.changePhase();
        // tree.changePhase();
        Environment.Weather sun = Environment.Weather.SUN;
        Environment.Weather rain = Environment.Weather.RAIN;
        for (int i = 0; i < 25; i++){
            System.out.println("Health: " + tree.getHealth());
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            System.out.println();
            tree.update();
        }
        System.out.println("Add water and sun: ");
        for (int i = 0; i < 10; i++){
            tree.bufferIncrease(sun);
            tree.bufferIncrease(rain);
            System.out.println("Health: " + tree.getHealth());
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            System.out.println();
        }
        System.out.println("Update every hour: ");

        for (int i = 0; i < 30; i++){
            tree.update();
            System.out.println("Health: " + tree.getHealth());
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            System.out.println();
        }
    }
    @Test
    public void test(){
        Tree tree = new Tree();
        Environment.Weather sun = Environment.Weather.SUN;
        Environment.Weather rain = Environment.Weather.RAIN;
        tree.changePhase();
        int n = 0;
        while (tree.update()) {
            System.out.println(++n);
            System.out.println("Health: " + tree.getHealth());
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            System.out.println();
            if (n > 25 ) {
                System.out.println("Add water and sun: ");
                tree.bufferIncrease(sun);
                tree.bufferIncrease(rain);
            }
        if(n > 100)
            break;
        }

    }

    @Test
    public void changeOfPhaseTest(){
        Tree tree = new Tree(); // SEED
        Environment.Weather sun = Environment.Weather.SUN;
        Environment.Weather rain = Environment.Weather.RAIN;
        for (int i = 1; i <= 200; i++) {
            tree.bufferIncrease(sun);
            System.out.println("Km: " + i);
            System.out.println("Phase " + tree.getTreePhase());
            System.out.println();
        }
    }
    @Test
    public void testIncrBuffersStore(){
        Tree tree = new Tree(); // SEED
        System.out.println("Sun " + tree.getSunLevel());
        System.out.println("Water " + tree.getWaterLevel());
        System.out.println();
        for(int i = 0; i < 10; i++){
            tree.update();
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            System.out.println();
        }
        Environment.Weather sun = Environment.Weather.SUN;
        Environment.Weather rain = Environment.Weather.RAIN;
        tree.bufferIncreaseStore(sun, 13);
        tree.bufferIncreaseStore(rain, 7);
        System.out.println("Sun " + tree.getSunLevel());
        System.out.println("Water " + tree.getWaterLevel());
        System.out.println();
    }

    @Test
    public void bufferTest(){

        //The buffer class keeps track of the buffer value that is an integer
        // between 0 and a max value that is dependent on the tree phase
        class Buffer implements Serializable {
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

        Buffer buffer = new Buffer(20);
        System.out.println("Buffer value: " + buffer.getValue());
        buffer.decrValue(5);
        System.out.println("Buffer value after decr: " + buffer.getValue());
        buffer.incrValue(7);
        System.out.println("Buffer value after incr: " + buffer.getValue());
        buffer.incrValue(7);
        System.out.println("Buffer value after incr: " + buffer.getValue());
        System.out.println();

        buffer.decrValue(9);
        System.out.println("Buffer value after decr: " + buffer.getValue());
        buffer.decrValue(17);
        System.out.println("Buffer value after decr: " + buffer.getValue());


    }

}