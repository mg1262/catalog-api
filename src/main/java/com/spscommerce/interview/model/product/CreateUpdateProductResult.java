package com.spscommerce.interview.model.product;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.model.Result;
import com.spscommerce.interview.model.Warning;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateUpdateProductResult extends Result<ProductEntity> {

    public CreateUpdateProductResult(ProductEntity result, List<Warning> warnings) {
        super(result, warnings);
    }
}
