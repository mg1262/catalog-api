package com.spscommerce.interview.service;

import com.spscommerce.interview.model.subscription.SubscriptionImportJob;
import com.spscommerce.interview.model.subscription.SubscriptionImportResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;


@Service
public class SubscriptionImportService {

    public SubscriptionImportResult importSubscriptions(File file) {
        // Not Implemented
        SubscriptionImportResult subscriptionImportResult = new SubscriptionImportResult();
        subscriptionImportResult.setImportId(UUID.randomUUID().toString());
        return subscriptionImportResult;
    }

}
