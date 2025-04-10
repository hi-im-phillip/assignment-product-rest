package org.app.assignment.productrest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PriceConverterApiException extends ApiException {

    public PriceConverterApiException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
