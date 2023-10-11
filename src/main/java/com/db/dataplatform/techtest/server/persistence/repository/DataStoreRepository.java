package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataStoreRepository extends JpaRepository<DataBodyEntity, Long> {
	
	List<DataBodyEntity> findByDataHeaderEntityIn(List<DataHeaderEntity> datHeaderEntities);
	
	Optional<DataBodyEntity> findByDataHeaderEntity(DataHeaderEntity datHeaderEntity);

}
