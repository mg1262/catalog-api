package com.spscommerce.interview.api.dto.common;

import com.spscommerce.interview.api.dto.DtoObject;
import lombok.Data;

@Data
public class ApiResponse {

    private String requestId;
    private Long timestamp;
    private Long responseTime;
    private int httpStatusCode;
    private boolean successful;
    private DtoObject data;

}
