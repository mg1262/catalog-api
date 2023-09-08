package com.spscommerce.interview.api.dto.organization;

import com.spscommerce.interview.api.dto.DtoObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationsDto extends DtoObject {

    private List<OrganizationDto> organizations;

}
