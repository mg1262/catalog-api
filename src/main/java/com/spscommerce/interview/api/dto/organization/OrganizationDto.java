package com.spscommerce.interview.api.dto.organization;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.subscription.SubscriptionDto;
import com.spscommerce.interview.api.dto.validation.*;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Data
public class OrganizationDto extends DtoObject {

    @NotNull(groups = {OnUpdateOrganization.class, OnCreateSubscription.class, OnUpdateSubscription.class})
    @Null(groups = OnCreateOrganization.class)
    @Size(min= 40 ,max = 40)
    @Pattern(regexp = "ORG-[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}")
    private String id;
    @NotBlank(groups = {OnCreateOrganization.class, OnUpdateOrganization.class})
    @Size(max = 200)
    private String name;
    @NotNull(groups = {OnCreateOrganization.class, OnUpdateOrganization.class})
    @Valid
    private LocationDto location;
    private List<SubscriptionDto> subscriptions;

}
