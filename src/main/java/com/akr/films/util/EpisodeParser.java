package com.akr.films.util;

import java.util.regex.Pattern;

import com.akr.films.domain.Episode;
import com.akr.films.exception.InvalidEpisodeException;

import lombok.experimental.UtilityClass;
@UtilityClass 
public class EpisodeParser {

    private static final Pattern NUMBER = Pattern.compile("\\d{1,2}");

    public static Episode parse(String value) {
        try {
            int number = value != null && NUMBER.matcher(value).matches()
                    ? Integer.parseInt(value)
                    : RomanNumeral.toInt(value);
            return new Episode(number);
        } catch (IllegalArgumentException e) {
            throw new InvalidEpisodeException(value);
        }
    }

}
