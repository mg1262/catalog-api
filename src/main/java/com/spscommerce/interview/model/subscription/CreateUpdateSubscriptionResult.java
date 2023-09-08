package com.spscommerce.interview.model.subscription;

import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.model.Result;
import com.spscommerce.interview.model.Warning;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateUpdateSubscriptionResult extends Result<SubscriptionEntity> {

    public CreateUpdateSubscriptionResult(SubscriptionEntity result, List<Warning> warnings) {
        super(result, warnings);
    }
}
