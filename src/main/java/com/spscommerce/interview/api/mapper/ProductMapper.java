package com.spscommerce.interview.api.mapper;

import com.spscommerce.interview.api.dto.product.ProductDto;
import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements EntityMapper<ProductDto, ProductEntity> {

    @Override
    public ProductEntity mapToDomain(ProductDto dtoObject) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(dtoObject.getId());
        productEntity.setName(dtoObject.getName());
        productEntity.setDescription(dtoObject.getDescription());
        productEntity.setPrice(dtoObject.getPrice());
        productEntity.setSubProducts(mapToDomain(dtoObject.getSubProducts()));
        return productEntity;
    }

    @Override
    public ProductDto mapToDto(ProductEntity domainObject) {
        ProductDto productDto = new ProductDto();
        productDto.setId(domainObject.getId());
        productDto.setName(domainObject.getName());
        productDto.setDescription(domainObject.getDescription());
        productDto.setPrice(domainObject.getPrice());
        productDto.setSubProducts(mapToDto(domainObject.getSubProducts()));
        return productDto;
    }
}
