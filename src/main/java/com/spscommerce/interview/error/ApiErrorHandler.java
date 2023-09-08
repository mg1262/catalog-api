package com.spscommerce.interview.error;

import com.spscommerce.interview.api.dto.common.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiErrorHandler {


    @ExceptionHandler(OrganizationManagementRuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(OrganizationManagementRuntimeException exception, HttpServletRequest request) {

        ApiError apiError = exception.getApiError();
        apiError.setRequestId(request.getAttribute("request_id").toString());
        apiError.setResponseTime(System.currentTimeMillis() - (Long)request.getAttribute("start_time"));
        apiError.setSuccessful(false);
        apiError.setHttpStatusCode(exception.getErrorCode().getHttpStatus().value());
        return ResponseEntity.status(exception.getErrorCode().getHttpStatus()).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {
        ApiError apiError = new ApiError();
        apiError.setRequestId(request.getAttribute("request_id").toString());
        apiError.setResponseTime(System.currentTimeMillis() - (Long)request.getAttribute("start_time"));
        apiError.setErrorCode("validation.error");
        apiError.setErrorDescription(exception.toString());
        apiError.setTimestamp(System.currentTimeMillis());
        apiError.setSuccessful(false);
        apiError.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBindException(BindException exception, HttpServletRequest request) {
        ApiError apiError = new ApiError();
        apiError.setRequestId(request.getAttribute("request_id").toString());
        apiError.setResponseTime(System.currentTimeMillis() - (Long)request.getAttribute("start_time"));
        apiError.setErrorCode("validation.error");
        StringBuilder stringBuilder = new StringBuilder();
        exception.getAllErrors().forEach(error -> {
            stringBuilder.append("[").append(((FieldError) error).getField()).append(": ").append(error.getDefaultMessage()).append("]");
        });
        apiError.setErrorDescription(stringBuilder.toString());
        apiError.setTimestamp(System.currentTimeMillis());
        apiError.setSuccessful(false);
        apiError.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception exception, HttpServletRequest request) {

        ApiError apiError = new ApiError();
        apiError.setRequestId(request.getAttribute("request_id").toString());
        apiError.setResponseTime(System.currentTimeMillis() - (Long)request.getAttribute("start_time"));
        apiError.setSuccessful(false);
        apiError.setErrorCode("internal.error");
        apiError.setErrorDescription(exception.getMessage());
        apiError.setHttpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

}
