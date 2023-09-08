package com.spscommerce.interview.model.subscription;

import com.spscommerce.interview.model.ImportState;
import lombok.Data;

@Data
public class SubscriptionImportJob {

    private String importId;
    private ImportState importState;
    private Double percentageComplete;
    private String failureMessage;

}
