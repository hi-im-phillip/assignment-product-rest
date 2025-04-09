package org.app.assignment.productrest.service.impl;

import lombok.RequiredArgsConstructor;
import org.app.assignment.productrest.api.ConverterApiClient;
import org.app.assignment.productrest.dto.HnbExchangeRateDTO;
import org.app.assignment.productrest.service.CurrencyConverter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class HnbCurrencyConverter implements CurrencyConverter {

    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final String SEPARATOR = ",";

    private final ConverterApiClient<HnbExchangeRateDTO> converterApiClient;

    @Override
    public BigDecimal convert(BigDecimal amount, String currency) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be a positive number.");
        }

        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Currency must not be null or empty.");
        }

        HnbExchangeRateDTO exchangeRate = converterApiClient.getExchangeRate(currency);

        if (exchangeRate == null) {
            throw new IllegalArgumentException("Currency not supported: " + currency);
        }

        String middleRateStr = exchangeRate.getMiddleRate();
        if (middleRateStr == null || middleRateStr.trim().isEmpty()) {
            throw new IllegalStateException("Middle exchange rate is missing for currency: " + currency);
        }

        try {
            BigDecimal middleExchangeRate = new BigDecimal(middleRateStr.replace(SEPARATOR, "."));
            return amount.multiply(middleExchangeRate).setScale(DEFAULT_SCALE, ROUNDING_MODE);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid exchange rate format: " + middleRateStr, e);
        }
    }
}
