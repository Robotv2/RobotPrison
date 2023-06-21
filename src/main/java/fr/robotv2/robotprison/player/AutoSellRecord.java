package fr.robotv2.robotprison.player;

public class AutoSellRecord {

    private int blockMined;
    private double earnings;

    public int getBlockMined() {
        return blockMined;
    }

    public void incrementBlockMined(int value) {
        blockMined += value;
    }

    public double getEarnings() {
        return earnings;
    }

    public void incrementEarnings(double value) {
        earnings += value;
    }

    public void reset() {
        blockMined = 0;
        earnings = 0;
    }
}
