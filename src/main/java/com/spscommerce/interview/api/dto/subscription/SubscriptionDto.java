package com.spscommerce.interview.api.dto.subscription;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.organization.OrganizationDto;
import com.spscommerce.interview.api.dto.product.ProductDto;
import com.spscommerce.interview.api.dto.validation.OnCreateOrganization;
import com.spscommerce.interview.api.dto.validation.OnCreateSubscription;
import com.spscommerce.interview.api.dto.validation.OnUpdateSubscription;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class SubscriptionDto extends DtoObject {

    @NotNull(groups = {OnUpdateSubscription.class})
    @Null(groups = {OnCreateSubscription.class, OnCreateOrganization.class})
    @Size(min= 40 ,max = 40)
    @Pattern(regexp = "SUB-[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}")
    private String id;
    @NotNull(groups = {OnCreateSubscription.class, OnUpdateSubscription.class})
    private OrganizationDto organization;
    @NotNull(groups = {OnCreateSubscription.class, OnUpdateSubscription.class})
    @Min(0)
    private double discount;
    @NotNull(groups = {OnCreateSubscription.class, OnUpdateSubscription.class})
    @Min(0)
    private double totalPrice;
    private List<ProductDto> products;

}
