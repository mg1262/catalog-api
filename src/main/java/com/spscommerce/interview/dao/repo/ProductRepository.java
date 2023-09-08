package com.spscommerce.interview.dao.repo;

import com.spscommerce.interview.dao.entity.ProductEntity;
import com.spscommerce.interview.dao.entity.SubscriptionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity, String> {

    @Query( "select p.id from ProductEntity p where p.id in :ids" )
    List<String> findExistingIds(@Param("ids") Collection<String> ids);

    List<ProductEntity> findAllByIdIn(Collection<String> ids, Pageable pageable);

    List<ProductEntity> findAll(Pageable pageable);


}
