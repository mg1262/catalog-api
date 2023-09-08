package com.spscommerce.interview.service;

import com.spscommerce.interview.model.EntityType;
import java.util.UUID;

public abstract class BaseService {

    private final String identifierPrefix;

    public BaseService(String identifierPrefix) {
        this.identifierPrefix = identifierPrefix;
    }

    protected String generateId() {
        return this.identifierPrefix + "-" + UUID.randomUUID();
    }

    protected abstract EntityType supports();



}
