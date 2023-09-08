package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.organization.LocationDto;
import com.spscommerce.interview.api.dto.organization.OrganizationDto;
import com.spscommerce.interview.dao.entity.Location;
import com.spscommerce.interview.dao.entity.OrganizationEntity;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationMapper implements EntityMapper<OrganizationDto, OrganizationEntity> {

    private final SubscriptionMapper subscriptionMapper;

    @Override
    public OrganizationEntity mapToDomain(OrganizationDto dtoObject) {
        OrganizationEntity organizationEntity = new OrganizationEntity();
        organizationEntity.setId(dtoObject.getId());
        organizationEntity.setName(dtoObject.getName());
        organizationEntity.setLocation(new Location());
        organizationEntity.getLocation().setAddress(dtoObject.getLocation().getAddress());
        organizationEntity.getLocation().setCity(dtoObject.getLocation().getCity());
        organizationEntity.getLocation().setState(dtoObject.getLocation().getState());
        organizationEntity.getLocation().setZipCode(dtoObject.getLocation().getZipCode());
        dtoObject.getSubscriptions().forEach(subscriptionDto -> {
            subscriptionDto.setOrganization(new OrganizationDto());
            subscriptionDto.getOrganization().setId(dtoObject.getId());
        });
        organizationEntity.setSubscriptions(subscriptionMapper.mapToDomain(dtoObject.getSubscriptions()));
        return organizationEntity;

    }

    @Override
    public OrganizationDto mapToDto(OrganizationEntity domainObject) {
        OrganizationDto organizationDto = new OrganizationDto();
        organizationDto.setId(domainObject.getId());
        organizationDto.setName(domainObject.getName());
        organizationDto.setLocation(new LocationDto());
        organizationDto.getLocation().setAddress(domainObject.getLocation().getAddress());
        organizationDto.getLocation().setCity(domainObject.getLocation().getCity());
        organizationDto.getLocation().setState(domainObject.getLocation().getState());
        organizationDto.getLocation().setZipCode(domainObject.getLocation().getZipCode());
        organizationDto.setSubscriptions(subscriptionMapper.mapToDto(domainObject.getSubscriptions()));
        return organizationDto;
    }
}
