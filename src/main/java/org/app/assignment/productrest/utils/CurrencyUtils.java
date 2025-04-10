package org.app.assignment.productrest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyUtils {

    public static final int DEFAULT_SCALE = 2;
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final String COMMA_SEPARATOR = ",";
    private static final String DOT_SEPARATOR = ".";


    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Value cannot be null or empty");
        }

        String normalizedValue = value.replace(COMMA_SEPARATOR, DOT_SEPARATOR);
        return new BigDecimal(normalizedValue);
    }

    public static BigDecimal parseAndSetScale(String value) {
        return parseBigDecimal(value).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

}
