package com.spscommerce.interview.service;

import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.dao.repo.SubscriptionRepository;
import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.model.EntityType;
import com.spscommerce.interview.model.Warning;
import com.spscommerce.interview.model.product.CreateProductsResult;
import com.spscommerce.interview.model.subscription.CreateSubscriptionsResult;
import com.spscommerce.interview.model.subscription.CreateUpdateSubscriptionResult;
import com.spscommerce.interview.search.SearchResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubscriptionService extends BaseService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProductService productService;
    private final SearchService searchService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, ProductService productService, SearchService searchService) {
        super( "SUB");
        this.subscriptionRepository = subscriptionRepository;
        this.productService = productService;
        this.searchService = searchService;
    }

    public CreateUpdateSubscriptionResult createSubscription(SubscriptionEntity subscription) {
        CreateProductsResult createProductsResult = productService.createProducts(new HashSet<>(subscription.getProducts()));
        subscription.setProducts(createProductsResult.getResult());
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        this.searchService.updateDocument(savedSubscription.getOrganization().getId());
        return new CreateUpdateSubscriptionResult(savedSubscription, createProductsResult.getWarnings());
    }

    public CreateSubscriptionsResult createSubscriptions(List<SubscriptionEntity> subscriptions) {
        return createSubscriptions(subscriptions, null);
    }

    public CreateSubscriptionsResult createSubscriptions(List<SubscriptionEntity> subscriptions, String organizationId) {
        List<Warning> warnings = new ArrayList<>();
        // Find all subscriptions that have IDs populated
        List<SubscriptionEntity> subscriptionsWithIds = subscriptions.stream().filter(subscription -> subscription.getId() != null).collect(Collectors.toList());
        if (!subscriptionsWithIds.isEmpty() && organizationId == null) {
            // Existing Subscriptions are not allow when creating an Organization
            ErrorCodes.ORG_CREATE_EXISTING_SUB_NOT_ALLOWED.throwException();
            // Find all subscriptions in DB with those IDs
            Iterable<SubscriptionEntity> existingSubscriptions = subscriptionRepository.findAllById(subscriptionsWithIds.stream().map(SubscriptionEntity::getId).collect(Collectors.toList()));
            Iterator<SubscriptionEntity> existingSubscriptionsIterator = existingSubscriptions.iterator();
            int existingSubscriptionsCount = 0;
            if (existingSubscriptionsIterator.hasNext()) {
                while (existingSubscriptionsIterator.hasNext()) {
                    SubscriptionEntity subscription = existingSubscriptionsIterator.next();
                    // Check if any existing Subscriptions belong to another Organization
                    if (!subscription.getOrganization().getId().equals(organizationId)) {
                        ErrorCodes.ORG_CREATE_SUB_ASSOCIATED_WITH_OTHER.throwException();
                    }
                    existingSubscriptionsCount++;
                }
            }
            // Check if the number of subscriptions with ids matches what is in the DB.
            if (subscriptionsWithIds.size() != existingSubscriptionsCount) {
                ErrorCodes.SUB_NOT_FOUND_UPDATE_ORG.throwException();
            }
        }
        // For each Subscription persist the Products and Sub Products
        subscriptions.forEach(subscriptionEntity -> {
            CreateProductsResult createProductsResult = productService.createProducts(new HashSet<>(subscriptionEntity.getProducts()));
            subscriptionEntity.setProducts(createProductsResult.getResult());
            warnings.addAll(createProductsResult.getWarnings());
        });
        // Then persist each Subscription
        subscriptions.forEach(subscription -> {
            if (subscription.getId() == null) {
                subscription.setId(generateId());
            } else {
                warnings.add(Warning.builder().id(subscription.getId()).entityType(EntityType.SUBSCRIPTION).duplicate(true).created(false).message("Subscription already exists.").build());
            }
        });
        subscriptionRepository.saveAll(subscriptions);
        return new CreateSubscriptionsResult(subscriptions, warnings);
    }

    public SubscriptionEntity readSubscription(String id) {
        SubscriptionEntity subscriptionEntity = this.subscriptionRepository.findById(id).orElse(null);
        if (subscriptionEntity == null) {
            ErrorCodes.SUB_READ_NOT_FOUND.throwException();
        }
        return subscriptionEntity;
    }

    public CreateUpdateSubscriptionResult updateSubscription(SubscriptionEntity subscription) {
        // Validation Request
        validateUpdate(subscription);
        // Persist Products and Sub Products first
        CreateProductsResult createProductsResult = productService.createProducts(new HashSet<>(subscription.getProducts()));
        subscription.setProducts(createProductsResult.getResult());
        // Persist Subscription
        SubscriptionEntity savedSubscription = subscriptionRepository.save(subscription);
        // Update the indexes
        this.searchService.updateDocument(savedSubscription.getOrganization().getId());
        return new CreateUpdateSubscriptionResult(savedSubscription, createProductsResult.getWarnings());
    }

    public void deleteSubscription(String id) {
        validateDelete(id);
        this.subscriptionRepository.deleteById(id);
    }

    public SearchResult<SubscriptionEntity> search(String query, int page, int limit, String sortColumn, boolean sortAsc) {
        if (query != null && !query.trim().isEmpty()) {
            Set<String> ids = this.searchService.search(supports(), query);
            SearchResult<SubscriptionEntity> searchResult = new SearchResult<>();
            searchResult.setTotalResults(ids.size());
            searchResult.setPage(page);
            searchResult.setLimit(limit);
            if (!ids.isEmpty()) {
                searchResult.setTotalPages(Double.valueOf(Math.ceil(ids.size() / (limit * 1d))).intValue());
                Pageable pageable = sortAsc ? PageRequest.of(page, limit, Sort.by(sortColumn).ascending()) :
                        PageRequest.of(page, limit, Sort.by(sortColumn).descending());
                List<SubscriptionEntity> entities = subscriptionRepository.findAllByIdIn(ids, pageable);
                searchResult.setResults(entities);
            }
            return searchResult;
        } else {
            long count = subscriptionRepository.count();
            SearchResult<SubscriptionEntity> searchResult = new SearchResult<>();
            searchResult.setTotalResults(Long.valueOf(count).intValue());
            searchResult.setPage(page);
            searchResult.setLimit(limit);
            if (count > 0) {
                searchResult.setTotalPages(Double.valueOf(Math.ceil(count / (limit * 1d))).intValue());
                Pageable pageable = sortAsc ? PageRequest.of(page, limit, Sort.by(sortColumn).ascending()) :
                        PageRequest.of(page, limit, Sort.by(sortColumn).descending());
                List<SubscriptionEntity> entities = subscriptionRepository.findAll(pageable);
                searchResult.setResults(entities);
            }
            return searchResult;
        }
    }

    @Override
    protected EntityType supports() {
        return EntityType.SUBSCRIPTION;
    }


    public void validateSubscriptions(List<SubscriptionEntity> subscriptionEntities) {
        Map<String, String> subscriptionOrganzationMap = new HashMap<>();
        subscriptionEntities.forEach(subscriptionEntity -> subscriptionOrganzationMap.put(subscriptionEntity.getId(), subscriptionEntity.getOrganization().getId()));
        Iterable<SubscriptionEntity> existingSubscriptions = subscriptionRepository.findAllById(subscriptionOrganzationMap.keySet());
        existingSubscriptions.forEach(subscriptionEntity -> {
            if (!subscriptionEntity.getOrganization().getId().equals(subscriptionOrganzationMap.get(subscriptionEntity.getId()))) {
                ErrorCodes.ORG_CREATE_SUB_ASSOCIATED_WITH_OTHER.throwException();
            }
        });
    }

    private void validateUpdate(SubscriptionEntity subscription) {
        if (!subscriptionRepository.existsById(subscription.getId())) {
            ErrorCodes.SUB_UPDATE_NOT_FOUND.throwException();
        }
    }

    private void validateDelete(String id) {
        if (!subscriptionRepository.existsById(id)) {
            ErrorCodes.SUB_DELETE_NOT_FOUND.throwException();
        }
    }


}
