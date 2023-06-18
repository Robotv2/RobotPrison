package fr.robotv2.robotprison.util;

import java.text.DecimalFormat;

public class NumberUtil {

    private final DecimalFormat FORMAT = new DecimalFormat("");

    private NumberUtil() { }

    public static double roundDecimal(double value, int decimal) {
        final int multiplier = 10^decimal;
        return (double) Math.round(value * multiplier) / multiplier;
    }
}
