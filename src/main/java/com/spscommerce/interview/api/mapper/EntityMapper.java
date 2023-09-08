package com.spscommerce.interview.api.mapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface EntityMapper<DTO, DOMAIN> {

    DOMAIN mapToDomain(DTO dtoObject);

    DTO mapToDto(DOMAIN domainObject);

    default List<DOMAIN> mapToDomain(List<DTO> dtoObjects) {
        if (dtoObjects == null || dtoObjects.isEmpty())
            return null;
        return dtoObjects.stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    default List<DTO> mapToDto(List<DOMAIN> domainObjects) {
        if (domainObjects == null || domainObjects.isEmpty())
            return null;
        return domainObjects.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    default List<DTO> mapToDto(Iterable<DOMAIN> domainIterable) {
        if (domainIterable == null) {
            return null;
        }
        return StreamSupport.stream(domainIterable.spliterator(), false).map(this::mapToDto).collect(Collectors.toList());
    }


}
