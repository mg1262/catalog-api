package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.subscription.ImportResponseDto;
import com.spscommerce.interview.model.subscription.SubscriptionImportResult;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionImportResponseMapper implements EntityMapper<ImportResponseDto, SubscriptionImportResult> {
    @Override
    public SubscriptionImportResult mapToDomain(ImportResponseDto dtoObject) {
        return null;
    }

    @Override
    public ImportResponseDto mapToDto(SubscriptionImportResult domainObject) {
        ImportResponseDto importResponseDto = new ImportResponseDto();
        importResponseDto.setImportId(domainObject.getImportId());
        return importResponseDto;
    }
}
