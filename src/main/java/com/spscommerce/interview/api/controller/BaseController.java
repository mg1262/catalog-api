package com.spscommerce.interview.api.controller;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected ApiResponse createSuccessfulResponse(DtoObject data, HttpServletRequest request) {
        ApiResponse response = new ApiResponse();
        response.setRequestId(request.getAttribute("request_id").toString());
        response.setResponseTime(System.currentTimeMillis() - (Long)request.getAttribute("start_time"));
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        response.setSuccessful(true);
        response.setHttpStatusCode(HttpStatus.OK.value());
        return response;
    }

}
