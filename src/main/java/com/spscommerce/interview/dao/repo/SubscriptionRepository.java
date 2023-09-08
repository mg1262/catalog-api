package com.spscommerce.interview.dao.repo;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, String> {

    List<SubscriptionEntity> findAllByIdIn(Collection<String> ids, Pageable pageable);

    List<SubscriptionEntity> findAll(Pageable pageable);


}
