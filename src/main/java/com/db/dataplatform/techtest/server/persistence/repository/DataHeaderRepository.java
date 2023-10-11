package com.db.dataplatform.techtest.server.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {
	
	List<DataHeaderEntity> findByBlocktype(BlockTypeEnum blockTypeEnum);
	
	DataHeaderEntity findByName(String blockName);

}
