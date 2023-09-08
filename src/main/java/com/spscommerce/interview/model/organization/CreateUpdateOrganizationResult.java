package com.spscommerce.interview.model.organization;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.model.Result;
import com.spscommerce.interview.model.Warning;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateUpdateOrganizationResult extends Result<OrganizationEntity> {

    public CreateUpdateOrganizationResult(OrganizationEntity result, List<Warning> warnings) {
        super(result, warnings);
    }
}
