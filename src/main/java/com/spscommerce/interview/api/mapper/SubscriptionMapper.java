package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.organization.OrganizationDto;
import com.spscommerce.interview.api.dto.subscription.SubscriptionDto;
import com.spscommerce.interview.dao.entity.Location;
import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionMapper implements EntityMapper<SubscriptionDto, SubscriptionEntity> {

    private final ProductMapper productMapper;

    @Override
    public SubscriptionEntity mapToDomain(SubscriptionDto dtoObject) {
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setId(dtoObject.getId());
        subscriptionEntity.setDiscount(dtoObject.getDiscount());
        subscriptionEntity.setTotalPrice(dtoObject.getTotalPrice());
        subscriptionEntity.setOrganization(new OrganizationEntity());
        subscriptionEntity.getOrganization().setId(dtoObject.getOrganization().getId());
        subscriptionEntity.setProducts(productMapper.mapToDomain(dtoObject.getProducts()));
        return subscriptionEntity;
    }

    @Override
    public SubscriptionDto mapToDto(SubscriptionEntity daoObject) {
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setId(daoObject.getId());
        subscriptionDto.setDiscount(daoObject.getDiscount());
        subscriptionDto.setTotalPrice(daoObject.getTotalPrice());
        subscriptionDto.setOrganization(new OrganizationDto());
        subscriptionDto.getOrganization().setId(daoObject.getOrganization().getId());
        subscriptionDto.getOrganization().setName(daoObject.getOrganization().getName());
        subscriptionDto.setProducts(productMapper.mapToDto(daoObject.getProducts()));
        return subscriptionDto;
    }
}
