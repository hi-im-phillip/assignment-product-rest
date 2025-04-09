package org.app.assignment.productrest.api;

import org.app.assignment.productrest.dto.HnbExchangeRateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
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
        try {
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam(CURRENCY_CODE_PARAM, currencyCode)
                    .toUriString();

            HnbExchangeRateDTO[] response = restTemplate.getForObject(url, HnbExchangeRateDTO[].class);

            if (response == null || response.length == 0) {
                return null;
            }

            return response[0];

        } catch (RestClientException e) {
            return null;
        }
    }
}
