package com.akr.swapi.util;

public final class RomanNumeral {

    public static final int MIN = 1;
    public static final int MAX = 10;

    private static final int[] VALUES = { 10, 9, 5, 4, 1 };
    private static final String[] SYMBOLS = { "X", "IX", "V", "IV", "I" };

    private RomanNumeral() {
    }

    public static String toRoman(int number) {
        if (number < MIN || number > MAX) {
            throw new IllegalArgumentException("Number must be between " + MIN + " and " + MAX + ": " + number);
        }
        StringBuilder roman = new StringBuilder();
        int remaining = number;
        for (int i = 0; i < VALUES.length; i++) {
            while (remaining >= VALUES[i]) {
                roman.append(SYMBOLS[i]);
                remaining -= VALUES[i];
            }
        }
        return roman.toString();
    }

}
