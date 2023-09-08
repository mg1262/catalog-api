package com.spscommerce.interview.service;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import com.spscommerce.interview.dao.repo.OrganizationRepository;
import com.spscommerce.interview.error.ErrorCodes;
import com.spscommerce.interview.model.EntityType;
import com.spscommerce.interview.model.organization.CreateUpdateOrganizationResult;
import com.spscommerce.interview.model.subscription.CreateSubscriptionsResult;
import com.spscommerce.interview.search.SearchResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrganizationService extends BaseService {

    private final OrganizationRepository organizationRepository;
    private final SubscriptionService subscriptionService;
    private final SearchService searchService;

    public OrganizationService(OrganizationRepository organizationRepository, SubscriptionService subscriptionService, SearchService searchService) {
        super("ORG");
        this.organizationRepository = organizationRepository;
        this.subscriptionService = subscriptionService;
        this.searchService = searchService;
    }

    public CreateUpdateOrganizationResult createOrganization(OrganizationEntity newOrganization) {
        validateCreate(newOrganization);
        newOrganization.setId(generateId());
        if (newOrganization.hasSubscriptions()) {
            List<SubscriptionEntity> subscriptions = newOrganization.getSubscriptions();
            newOrganization.setSubscriptions(null);
            OrganizationEntity organizationEntity = organizationRepository.save(newOrganization);
            subscriptions.forEach(subscription -> subscription.setOrganization(organizationEntity));
            CreateSubscriptionsResult createSubscriptionsResult = subscriptionService.createSubscriptions(subscriptions);
            organizationEntity.setSubscriptions(createSubscriptionsResult.getResult());
            searchService.addDocument(organizationEntity);
            return new CreateUpdateOrganizationResult(organizationEntity, createSubscriptionsResult.getWarnings());
        } else {
            OrganizationEntity organizationEntity = organizationRepository.save(newOrganization);
            searchService.addDocument(organizationEntity);
            return new CreateUpdateOrganizationResult(organizationEntity, new ArrayList<>());
        }
    }

    public OrganizationEntity readOrganization(String id) {
        OrganizationEntity organizationEntity = this.organizationRepository.findById(id).orElse(null);
        if (organizationEntity == null) {
            ErrorCodes.ORG_READ_NOT_FOUND.throwException();
        }
        return organizationEntity;
    }

    public CreateUpdateOrganizationResult updateOrganization(OrganizationEntity organization) {
        validateUpdate(organization);
        List<SubscriptionEntity> subscriptions = organization.getSubscriptions();
        organization.setSubscriptions(null);
        OrganizationEntity organizationEntity = organizationRepository.save(organization);
        CreateSubscriptionsResult createSubscriptionsResult = subscriptionService.createSubscriptions(subscriptions, organization.getId());
        organizationEntity.setSubscriptions(createSubscriptionsResult.getResult());
        searchService.addDocument(organizationEntity);
        return new CreateUpdateOrganizationResult(organizationEntity, createSubscriptionsResult.getWarnings());
    }

    public void deleteOrganization(String id) {
        validateDelete(id);
        this.organizationRepository.findById(id).ifPresent(organizationEntity -> {
            organizationEntity.getSubscriptions().forEach(subscription -> this.subscriptionService.deleteSubscription(subscription.getId()));
            this.organizationRepository.deleteById(id);
        });
    }

    public SearchResult<OrganizationEntity> search(String query, int page, int limit, String sortColumn, boolean sortAsc) {
        if (query != null && !query.trim().isEmpty()) {
            Set<String> ids = this.searchService.search(supports(), query);
            SearchResult<OrganizationEntity> searchResult = new SearchResult<>();
            searchResult.setTotalResults(ids.size());
            searchResult.setPage(page);
            searchResult.setLimit(limit);
            if (!ids.isEmpty()) {
                searchResult.setTotalPages(Double.valueOf(Math.ceil(ids.size() / (limit * 1d))).intValue());
                Pageable pageable = sortAsc ? PageRequest.of(page, limit, Sort.by(sortColumn).ascending()) :
                        PageRequest.of(page, limit, Sort.by(sortColumn).descending());
                List<OrganizationEntity> entities = organizationRepository.findAllByIdIn(ids, pageable);
                searchResult.setResults(entities);
            }
            return searchResult;
        } else {
            long count = organizationRepository.count();
            SearchResult<OrganizationEntity> searchResult = new SearchResult<>();
            searchResult.setTotalResults(Long.valueOf(count).intValue());
            searchResult.setPage(page);
            searchResult.setLimit(limit);
            if (count > 0) {
                searchResult.setTotalPages(Double.valueOf(Math.ceil(count / (limit * 1d))).intValue());
                Pageable pageable = sortAsc ? PageRequest.of(page, limit, Sort.by(sortColumn).ascending()) :
                        PageRequest.of(page, limit, Sort.by(sortColumn).descending());
                List<OrganizationEntity> entities = organizationRepository.findAll(pageable);
                searchResult.setResults(entities);
            }
            return searchResult;
        }
    }

    @Override
    protected EntityType supports() {
        return EntityType.ORGANIZATION;
    }

    private void validateCreate(OrganizationEntity organization) {
        if (organization.getSubscriptions() != null && !organization.getSubscriptions().isEmpty()) {
            subscriptionService.validateSubscriptions(organization.getSubscriptions());
        }
    }

    private void validateUpdate(OrganizationEntity organization) {
        if (!organizationRepository.existsById(organization.getId())) {
            ErrorCodes.ORG_UPDATE_NOT_FOUND.throwException();
        }
    }

    private void validateDelete(String id) {
        if (!organizationRepository.existsById(id)) {
            ErrorCodes.ORG_DELETE_NOT_FOUND.throwException();
        }
    }


}
