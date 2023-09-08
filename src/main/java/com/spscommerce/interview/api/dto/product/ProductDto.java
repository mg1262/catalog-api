package com.spscommerce.interview.api.dto.product;

import com.spscommerce.interview.api.dto.DtoObject;
import com.spscommerce.interview.api.dto.validation.OnCreateProduct;

import com.spscommerce.interview.api.dto.validation.OnProductAssociation;
import com.spscommerce.interview.api.dto.validation.OnUpdateProduct;
import lombok.Data;


import javax.validation.constraints.*;
import java.util.List;

@Data
public class ProductDto extends DtoObject {

    @Null(groups = OnCreateProduct.class)
    @NotNull(groups = {OnUpdateProduct.class, OnProductAssociation.class})
    @Pattern(regexp = "PRD-[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}")
    @Size(min= 40 ,max = 40)
    private String id;
    @NotBlank(groups = {OnCreateProduct.class, OnUpdateProduct.class})
    @Size(max = 200)
    private String name;
    @NotBlank(groups = {OnCreateProduct.class, OnUpdateProduct.class})
    @Size(max = 200)
    private String description;
    @NotNull(groups = {OnCreateProduct.class, OnUpdateProduct.class})
    @Min(0)
    private double price;
    private List<ProductDto> subProducts;

}
