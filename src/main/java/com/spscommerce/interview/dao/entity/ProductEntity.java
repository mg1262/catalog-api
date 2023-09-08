package com.spscommerce.interview.dao.entity;


import com.spscommerce.interview.model.CatalogEntity;
import com.spscommerce.interview.model.EntityType;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class ProductEntity implements CatalogEntity {

    @Id
    private String id;
    private String name;
    private String description;
    private double price;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "product_sub_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_product_id"))
    private List<ProductEntity> subProducts;


    @Override
    public EntityType getType() {
        return EntityType.PRODUCT;
    }
}
