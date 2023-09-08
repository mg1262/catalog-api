package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.organization.CreateUpdateOrganizationResultDto;
import com.spscommerce.interview.model.organization.CreateUpdateOrganizationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUpdateOrganizationResultMapper implements EntityMapper<CreateUpdateOrganizationResultDto, CreateUpdateOrganizationResult> {

    private final OrganizationMapper organizationMapper;

    @Override
    public CreateUpdateOrganizationResult mapToDomain(CreateUpdateOrganizationResultDto createUpdateOrganizationResultDto) {
        return null;
    }

    @Override
    public CreateUpdateOrganizationResultDto mapToDto(CreateUpdateOrganizationResult createUpdateOrganizationResult) {
        CreateUpdateOrganizationResultDto createUpdateOrganizationResultDto = new CreateUpdateOrganizationResultDto();
        createUpdateOrganizationResultDto.setOrganization(organizationMapper.mapToDto(createUpdateOrganizationResult.getResult()));
        createUpdateOrganizationResultDto.setWarnings(createUpdateOrganizationResult.getWarnings());
        return createUpdateOrganizationResultDto;
    }
}
