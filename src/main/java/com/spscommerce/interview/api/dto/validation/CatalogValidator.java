package com.spscommerce.interview.api.dto.validation;

import com.spscommerce.interview.api.dto.product.ProductDto;
import com.spscommerce.interview.api.dto.subscription.SubscriptionDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

import javax.validation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CatalogValidator {

    private final SmartValidator validator;


    public void validateSubscriptions(List<SubscriptionDto> subscriptionDtos, BindingResult bindingResult) {
        if (subscriptionDtos != null) {
            for (SubscriptionDto subscriptionDto : subscriptionDtos) {
                validateProducts(subscriptionDto.getProducts(), bindingResult);
            }
        }
    }

    @SneakyThrows
    public void validateProducts(List<ProductDto> productDtos, BindingResult bindingResult) {
        if (productDtos != null) {
            for (ProductDto productDto : productDtos) {
                Class<?> clazz = productDto.getId() != null ? OnProductAssociation.class : OnCreateProduct.class;
                validator.validate(productDto, bindingResult, clazz);
                validator.validate(productDto, bindingResult);
                if (bindingResult.hasErrors()) {
                    throw new BindException(bindingResult);
                }
                validateProducts(productDto.getSubProducts(), bindingResult);
            }
        }
    }

}
