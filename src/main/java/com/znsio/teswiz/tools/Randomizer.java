package com.znsio.teswiz.tools;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.rng.simple.RandomSource;

public class Randomizer {

    private Randomizer() {}

    public static String randomize(int size) {
        return RandomStringUtils.secureStrong().nextNumeric(size);
    }

    public static String randomize(String randomizeTestData) {
        String randomizedValue;
        try {
            Long.parseLong(randomizeTestData);
            randomizedValue = "80" + RandomStringUtils.secureStrong().nextNumeric(8);
        } catch (NumberFormatException nfe) {
            randomizedValue = "e2e_" + RandomStringUtils.secureStrong().nextAlphanumeric(10) + "@getnada.com";
        }
        return randomizedValue;
    }

    public static String randomizeAlphaNumericString(int stringLength) {
        return RandomStringUtils.secureStrong().nextAlphanumeric(stringLength);
    }

    public static String randomizeString(int stringLength) {
        return RandomStringUtils.secureStrong().nextAlphabetic(stringLength);
    }

    public static int getRandomNumberBetween(int min, int max) {
        return RandomSource.XO_RO_SHI_RO_128_PP.create().nextInt(min, max);
    }

    public static long getRandomNumberBetween(long min, long max) {
        return RandomSource.XO_RO_SHI_RO_128_PP.create().nextLong(min, max);
    }
}
