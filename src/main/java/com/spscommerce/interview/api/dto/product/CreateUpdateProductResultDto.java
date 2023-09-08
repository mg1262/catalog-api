package com.spscommerce.interview.api.dto.product;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.organization.OrganizationDto;
import com.spscommerce.interview.model.Warning;
import lombok.Data;

import java.util.List;

@Data
public class CreateUpdateProductResultDto extends DtoObject {

    private ProductDto product;
    private List<Warning> warnings;

}
