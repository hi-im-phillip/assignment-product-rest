package org.app.assignment.productrest.service;

import java.math.BigDecimal;

public interface CurrencyConverter {

    BigDecimal convert(BigDecimal amount, String currency);
}
