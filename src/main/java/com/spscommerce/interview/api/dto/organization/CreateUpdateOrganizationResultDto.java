package com.spscommerce.interview.api.dto.organization;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.model.Warning;
import lombok.Data;

import java.util.List;

@Data
public class CreateUpdateOrganizationResultDto extends DtoObject {

    private OrganizationDto organization;
    private List<Warning> warnings;

}
