package com.spscommerce.interview.dao.entity;

import com.spscommerce.interview.model.CatalogEntity;
import com.spscommerce.interview.model.EntityType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "organization")
public class OrganizationEntity implements CatalogEntity {

    @Id
    private String id;

    private String name;

    private Location location;

    @OneToMany(mappedBy = "organization")
    private List<SubscriptionEntity> subscriptions;

    public boolean hasSubscriptions() {
        return subscriptions != null && !subscriptions.isEmpty();
    }

    @Override
    public EntityType getType() {
        return EntityType.ORGANIZATION;
    }
}
