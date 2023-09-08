package com.spscommerce.interview.dao.repo;

import com.spscommerce.interview.dao.entity.OrganizationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrganizationRepository extends CrudRepository<OrganizationEntity, String> {
//    @Query( "select o from OrganizationEntity o where o.id in :ids" )
    List<OrganizationEntity> findAllByIdIn(Collection<String> ids, Pageable pageable);

    List<OrganizationEntity> findAll(Pageable pageable);

}
