package com.spscommerce.interview.api.inceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private SecureRandom secureRandom;

    public RequestInterceptor() {
        this.secureRandom = new SecureRandom();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("request_id", generateRequestId());
        request.setAttribute("start_time", System.currentTimeMillis());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private String generateRequestId() {
        return new StringBuilder().append(System.currentTimeMillis()).append(this.secureRandom.nextInt(10000)).toString();
    }

}
