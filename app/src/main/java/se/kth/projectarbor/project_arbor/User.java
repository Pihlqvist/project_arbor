package se.kth.projectarbor.project_arbor;

/**
 * Created by pethrus on 2017-04-27.
 */

public class User {

    private static int SEED_AMOUNT_PER_KM = 1;
    private static int SPROUT_AMOUNT_PER_KM = 2;
    private static int SAPLING_AMOUNT_PER_KM = 3;
    private static int GROWN_TREE_AMOUNT_PER_KM = 4;

    private int money;

    public User(){
        money = 0;
    }
    // Called every km
    public void update(Tree.Phase phase, boolean incr){
            addMoney();
    }
        // Add correct amount of money depending on phase.
        private void addMoney(Tree.Phase phase) {
            switch (phase) {
                case (SEED):
                    this.money += SEED_AMOUNT_PER_KM;
                    break;
                case (SPROUT):
                    this.money += SPROUT_AMOUNT_PER_KM;
                    break;
                case (SAPLING):
                    this.money += SAPLING_AMOUNT_PER_KM;
                    break;
                case (GROWN_TREE):
                    this.money += GROWN_TREE_AMOUNT_PER_KM;
                    break;
            }
        }
    // Called from store when purchase has been made
    public String buy(int amount){
        this.money -= amount;
        if (this.money < 0)
            return "Out of money";
        return "Buy succesful";
    }


}
