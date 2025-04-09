package org.app.assignment.productrest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class HnbApiException extends ApiException {

    public HnbApiException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
