package com.akr.swapi.domain;

import com.akr.swapi.util.RomanNumeral;

public final class Episode {

    private final int number;
    private final String roman;

    public Episode(int number) {
        this.roman = RomanNumeral.toRoman(number);
        this.number = number;
    }

    public int toInt() {
        return number;
    }

    @Override
    public String toString() {
        return roman;
    }

}
