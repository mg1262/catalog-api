package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.SearchResultDto;
import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.search.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SearchResultMapper<DTO extends DtoObject,DOMAIN> implements EntityMapper<SearchResultDto<DTO>, SearchResult<DOMAIN>> {

    private final OrganizationMapper organizationMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final ProductMapper productMapper;
    private Map<Class<?>, EntityMapper> entityMapperMap;

    @PostConstruct
    public void init() {
        this.entityMapperMap = new HashMap<>();
        this.entityMapperMap.put(OrganizationEntity.class, this.organizationMapper);
        this.entityMapperMap.put(SubscriptionEntity.class, this.subscriptionMapper);
        this.entityMapperMap.put(ProductEntity.class, this.productMapper);

    }

    @Override
    public SearchResult<DOMAIN> mapToDomain(SearchResultDto<DTO> dtoObject) {
        return null;
    }

    @Override
    public SearchResultDto<DTO> mapToDto(SearchResult<DOMAIN> domainObject) {
        SearchResultDto<DTO> searchResultDto = new SearchResultDto<>();
        searchResultDto.setTotalResults(domainObject.getTotalResults());
        searchResultDto.setPage(domainObject.getPage());
        searchResultDto.setTotalPages(domainObject.getTotalPages());
        searchResultDto.setLimit(domainObject.getLimit());
        if (domainObject.getResults() != null && !domainObject.getResults().isEmpty()) {
            searchResultDto.setResults(this.entityMapperMap.get(domainObject.getResults().iterator().next().getClass()).mapToDto(domainObject.getResults()));
        } else {
            searchResultDto.setResults(new ArrayList<>());
        }
        return searchResultDto;
    }

}
