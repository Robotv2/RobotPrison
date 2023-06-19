package fr.robotv2.robotprison.util;

import java.text.DecimalFormat;

public class NumberUtil {

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    private NumberUtil() { }

    public static String formatNumber(Number value) {
        return DECIMAL_FORMAT.format(value.doubleValue());
    }

    public static double roundDecimal(double value, int decimal) {
        final int multiplier = 10^decimal;
        return (double) Math.round(value * multiplier) / multiplier;
    }
}
