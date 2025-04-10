package org.app.assignment.productrest.aspect;

import lombok.RequiredArgsConstructor;
import org.app.assignment.productrest.aspect.annotation.PriceValidation;
import org.app.assignment.productrest.exception.PriceConverterApiException;
import org.app.assignment.productrest.utils.CurrencyUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "price-validate.enabled", havingValue = "true")
public class PriceValidatorAspect {

    @Before("@annotation(org.app.assignment.productrest.aspect.annotation.PriceValidation)")
    public void validateAnnotatedMethod(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PriceValidation annotation = method.getAnnotation(PriceValidation.class);
        validateParameterWithFields(joinPoint.getArgs()[0], annotation.fieldName());
    }

    @Before("execution(* *(.., @org.app.assignment.productrest.aspect.annotation.PriceValidation (*), ..))")
    public void validateAnnotatedParameter(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            if (i < args.length && args[i] != null) {
                PriceValidation annotation = parameters[i].getAnnotation(PriceValidation.class);
                if (annotation != null) {
                    validateParameterWithFields(args[i], annotation.fieldName());
                }
            }
        }
    }

    private void validateParameterWithFields(Object dto, String fieldNameString) {
        if (dto == null) {
            return;
        }
        String[] fieldNames = fieldNameString.split(",");

        for (String fieldName : fieldNames) {
            fieldName = fieldName.trim(); // Remove any whitespace
            try {
                Field field = dto.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(dto);

                if (fieldValue instanceof String priceStr) {

                    if (!priceStr.trim().isEmpty()) {
                        try {
                            BigDecimal priceBigDecimal = CurrencyUtils.parseAndSetScale(priceStr);

                            if (!CurrencyUtils.isPositive(priceBigDecimal)) {
                                throw new PriceConverterApiException(HttpStatus.BAD_REQUEST,
                                        "Price must be positive: " + fieldName);
                            }

                            String normalizedPrice = priceBigDecimal.toString();

                            field.set(dto, normalizedPrice);
                        } catch (NumberFormatException e) {
                            throw new PriceConverterApiException(HttpStatus.BAD_REQUEST,
                                    "Invalid price format: " + priceStr);
                        }
                    }
                } else if (fieldValue instanceof BigDecimal price) {
                    if (!CurrencyUtils.isPositive(price)) {
                        throw new PriceConverterApiException(HttpStatus.BAD_REQUEST,
                                "Price must be positive: " + fieldName);
                    }
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new PriceConverterApiException(HttpStatus.BAD_REQUEST,
                        "Error validating price field: " + fieldName);
            }
        }
    }
}
