package com.akr.films.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.akr.films.exception.InvalidEpisodeException;

class EpisodeParserTest {

    @Test
    void shouldParseNumberOrRoman() {
        assertEquals(4, EpisodeParser.parse("4").toInt());
        assertEquals(4, EpisodeParser.parse("IV").toInt());
        assertEquals(4, EpisodeParser.parse("iv").toInt());
        assertEquals(10, EpisodeParser.parse("10").toInt());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(InvalidEpisodeException.class, () -> EpisodeParser.parse(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", "0", "11", "99999999999", "+4", " 4", "" })
    void shouldRejectInvalidValues(String invalid) {
        assertThrows(InvalidEpisodeException.class, () -> EpisodeParser.parse(invalid));
    }

}
