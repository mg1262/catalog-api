package com.spscommerce.interview.service;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.dao.repo.OrganizationRepository;
import com.spscommerce.interview.model.CatalogEntity;
import com.spscommerce.interview.model.EntityType;
import com.spscommerce.interview.search.Indexer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SearchService {

    private final OrganizationRepository organizationRepository;
    private final Map<EntityType, Indexer<CatalogEntity>> indexerMap;

    public SearchService(OrganizationRepository organizationRepository, List<Indexer> indexers) {
        this.organizationRepository = organizationRepository;
        this.indexerMap = new HashMap<>();
        indexers.forEach(indexer -> this.indexerMap.put(indexer.supports(), indexer));
    }

    @Async
    public void addDocument(CatalogEntity entity) {
        this.indexerMap.get(entity.getType()).addDocument(entity);
        switch (entity.getType()) {
            case ORGANIZATION -> {
                OrganizationEntity organizationEntity = (OrganizationEntity)entity;
                if (organizationEntity.getSubscriptions() != null) {
                    organizationEntity.getSubscriptions().forEach(subscription -> {
                        addDocument(subscription);
                        if (subscription.getProducts() != null) {
                            subscription.getProducts().forEach(this::addDocument);
                        }
                    });
                }
            }
            case SUBSCRIPTION -> {
                SubscriptionEntity subscriptionEntity = (SubscriptionEntity)entity;
                if (subscriptionEntity.getProducts() != null) {
                    subscriptionEntity.getProducts().forEach(this::addDocument);
                }
            }
        }

    }

    public Set<String> search(EntityType entityType, String query) {
        return this.indexerMap.get(entityType).search(query);
    }
    @Async
    public void updateDocument(String organizationId) {
        OrganizationEntity organizationEntity = this.organizationRepository.findById(organizationId).orElse(null);
        if (organizationEntity != null) {
            addDocument(organizationEntity);
        }
    }

    @Async
    public void updateDocument(ProductEntity productEntity) {
        StringBuilder sb = new StringBuilder("subscription.product.id:");
        sb.append(productEntity.getId()).append(" OR subscription.product.sub_product.id:").append(productEntity.getId());
        Set<String> organizations = search(EntityType.ORGANIZATION, sb.toString());
        organizations.forEach(this::updateDocument);
    }

}
