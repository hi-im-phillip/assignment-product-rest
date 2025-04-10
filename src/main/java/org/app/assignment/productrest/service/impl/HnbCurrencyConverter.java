package org.app.assignment.productrest.service.impl;

import lombok.RequiredArgsConstructor;
import org.app.assignment.productrest.api.ConverterApiClient;
import org.app.assignment.productrest.dto.HnbExchangeRateDTO;
import org.app.assignment.productrest.exception.ApiException;
import org.app.assignment.productrest.exception.HnbApiException;
import org.app.assignment.productrest.service.CurrencyConverter;
import org.app.assignment.productrest.utils.CurrencyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.app.assignment.productrest.utils.CurrencyUtils.DEFAULT_ROUNDING_MODE;
import static org.app.assignment.productrest.utils.CurrencyUtils.DEFAULT_SCALE;


@Service
@RequiredArgsConstructor
public class HnbCurrencyConverter implements CurrencyConverter {

    private final ConverterApiClient<HnbExchangeRateDTO> converterApiClient;

    @Override
    public BigDecimal convert(BigDecimal amount, String currency) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be a positive number.");
        }

        if (currency == null || currency.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Currency must not be null or empty.");
        }

        HnbExchangeRateDTO exchangeRate = converterApiClient.getExchangeRate(currency);

        if (exchangeRate == null) {
            throw new HnbApiException(HttpStatus.BAD_REQUEST, "Currency not supported: " + currency);
        }

        String middleRateStr = exchangeRate.getMiddleRate();
        if (middleRateStr == null || middleRateStr.trim().isEmpty()) {
            throw new HnbApiException(HttpStatus.NOT_FOUND, "Middle exchange rate is missing for currency: " + currency);
        }

        try {
            BigDecimal middleExchangeRate = CurrencyUtils.parseBigDecimal(middleRateStr);
            return amount.multiply(middleExchangeRate).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        } catch (NumberFormatException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid exchange rate format: " + middleRateStr);
        }
    }
}
