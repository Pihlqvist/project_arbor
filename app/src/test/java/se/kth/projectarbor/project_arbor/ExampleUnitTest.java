package se.kth.projectarbor.project_arbor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleUnitTest {
    @Test
    public void treeTest() {
        Tree tree = new Tree();
        tree.changePhase();
        for(int i = 0; i < 35; i++) {
            System.out.println("Health: " + tree.getHealth());
            System.out.println("Sun " + tree.getSunLevel());
            System.out.println("Water " + tree.getWaterLevel());
            tree.update();
        }
    }
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

}