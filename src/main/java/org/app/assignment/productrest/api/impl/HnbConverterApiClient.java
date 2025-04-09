package org.app.assignment.productrest.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.app.assignment.productrest.api.ConverterApiClient;
import org.app.assignment.productrest.dto.HnbExchangeRateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class HnbConverterApiClient implements ConverterApiClient<HnbExchangeRateDTO> {

    private static final String CURRENCY_CODE_PARAM = "valuta";

    private final RestTemplate restTemplate;

    @Value("${hnb.api.url}")
    private String apiUrl;

    public HnbConverterApiClient(
            RestTemplate restTemplate,
            @Value("${hnb.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public HnbExchangeRateDTO getExchangeRate(String currencyCode) {

        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            log.error("Currency code is null or empty");
            return null;
        }

        try {
            log.debug("Fetching exchange rate for currency: {}", currencyCode);

            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam(CURRENCY_CODE_PARAM, currencyCode)
                    .toUriString();

            HnbExchangeRateDTO[] response = restTemplate.getForObject(url, HnbExchangeRateDTO[].class);

            if (response == null || response.length == 0) {
                log.warn("No exchange rate found for currency: {}", currencyCode);
                return null;
            }

            if (response.length > 1) {
                log.warn("Multiple exchange rates found for currency: {}. Returning the first one.", currencyCode);
            }

            return response[0];

        } catch (RestClientException e) {
            log.error("Failed to fetch exchange rate for currency: {}", currencyCode, e);
            return null;
        }
    }
}
