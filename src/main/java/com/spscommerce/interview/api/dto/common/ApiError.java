package com.spscommerce.interview.api.dto.common;

import lombok.Data;

@Data
public class ApiError {

    private String requestId;
    private Long timestamp;
    private Long responseTime;
    private int httpStatusCode;
    private boolean successful;
    private String errorCode;
    private String errorDescription;

}
