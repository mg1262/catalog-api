package com.spscommerce.interview.api.dto.subscription;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.product.ProductDto;
import com.spscommerce.interview.model.Warning;
import lombok.Data;

import java.util.List;

@Data
public class CreateUpdateSubscriptionResultDto extends DtoObject {

    private SubscriptionDto subscription;
    private List<Warning> warnings;

}
