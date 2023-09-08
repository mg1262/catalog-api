package com.spscommerce.interview.model.product;

import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.model.Result;
import com.spscommerce.interview.model.Warning;

import java.util.List;

public class CreateProductsResult extends Result<List<ProductEntity>> {

    public CreateProductsResult(List<ProductEntity> result, List<Warning> warnings) {
        super(result, warnings);
    }

}
