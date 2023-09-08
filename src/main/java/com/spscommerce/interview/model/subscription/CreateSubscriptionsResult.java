package com.spscommerce.interview.model.subscription;

import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.model.Result;
import com.spscommerce.interview.model.Warning;

import java.util.List;

public class CreateSubscriptionsResult extends Result<List<SubscriptionEntity>> {

    public CreateSubscriptionsResult(List<SubscriptionEntity> result, List<Warning> warnings) {
        super(result, warnings);
    }

}
