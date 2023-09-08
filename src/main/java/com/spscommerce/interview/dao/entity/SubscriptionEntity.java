package com.spscommerce.interview.dao.entity;

import com.spscommerce.interview.model.CatalogEntity;
import com.spscommerce.interview.model.EntityType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "subscription")
public class SubscriptionEntity implements CatalogEntity {

    @Id
    private String id;

    private double discount;
    private double totalPrice;

    @ToString.Exclude
    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.REMOVE})
    @JoinColumn (name = "organization_id")
    private OrganizationEntity organization;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(
            name = "subscription_product",
            joinColumns = @JoinColumn(name = "subscription_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<ProductEntity> products;

    @Override
    public EntityType getType() {
        return EntityType.SUBSCRIPTION;
    }
}
