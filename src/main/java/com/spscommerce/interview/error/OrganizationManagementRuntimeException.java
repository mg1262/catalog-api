package com.spscommerce.interview.error;

import com.spscommerce.interview.api.dto.common.ApiError;
import lombok.Getter;

@Getter
public class OrganizationManagementRuntimeException extends RuntimeException {

    private final ErrorCodes errorCode;
    public OrganizationManagementRuntimeException(ErrorCodes errorCodes) {
        this.errorCode = errorCodes;
    }
    public OrganizationManagementRuntimeException(ErrorCodes errorCodes, Throwable e) {
        super(e);
        this.errorCode = errorCodes;
    }

    public ApiError getApiError() {
        ApiError apiError = new ApiError();
        apiError.setErrorCode(this.errorCode.getErrorCode());
        apiError.setErrorDescription(this.errorCode.getErrorDescription());
        apiError.setTimestamp(System.currentTimeMillis());
        return apiError;
    }
}
