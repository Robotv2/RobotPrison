package fr.robotv2.robotprison.util;

public class NumberUtil {

    public static double roundDecimal(double value, int decimal) {
        final int multiplier = 10^decimal;
        return (double) Math.round(value * multiplier) / multiplier;
    }
}
