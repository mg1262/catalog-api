package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.product.CreateUpdateProductResultDto;
import com.spscommerce.interview.model.product.CreateUpdateProductResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUpdateProductResultMapper implements EntityMapper<CreateUpdateProductResultDto, CreateUpdateProductResult> {

    private final ProductMapper productMapper;

    @Override
    public CreateUpdateProductResult mapToDomain(CreateUpdateProductResultDto createUpdateProductResultDto) {
        return null;
    }

    @Override
    public CreateUpdateProductResultDto mapToDto(CreateUpdateProductResult createUpdateProductResult) {
        CreateUpdateProductResultDto createUpdateProductResultDto = new CreateUpdateProductResultDto();
        createUpdateProductResultDto.setProduct(productMapper.mapToDto(createUpdateProductResult.getResult()));
        createUpdateProductResultDto.setWarnings(createUpdateProductResult.getWarnings());
        return createUpdateProductResultDto;
    }
}
