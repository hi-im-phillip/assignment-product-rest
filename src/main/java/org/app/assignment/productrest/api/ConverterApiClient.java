package org.app.assignment.productrest.api;

public interface ConverterApiClient<T> {
    T getExchangeRate(String currencyCode);
}
