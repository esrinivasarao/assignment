package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataBodyServiceImpl implements DataBodyService {

    private final DataStoreRepository dataStoreRepository;
    
    private final DataHeaderRepository dataHeaderRepository;

    @Override
    public void saveDataBody(DataBodyEntity dataBody) {
        dataStoreRepository.save(dataBody);
    }

    @Override
    public List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType) {
    	List<DataHeaderEntity> dataHeaderEntities = dataHeaderRepository.findByBlocktype(blockType);
    	List<DataBodyEntity> dataBodyEntities = dataStoreRepository.findByDataHeaderEntityIn(dataHeaderEntities);
        return dataBodyEntities;
    }

    @Override
    public Optional<DataBodyEntity> getDataByBlockName(String blockName) {
    	DataHeaderEntity dataHeaderEntity = dataHeaderRepository.findByName(blockName);
        return dataStoreRepository.findByDataHeaderEntity(dataHeaderEntity);
    }

	@Override
	public void updateBlockName(String name,String blockName) {
		DataHeaderEntity dataHeaderEntity = dataHeaderRepository.findByName(name);
		dataHeaderEntity.setBlocktype(BlockTypeEnum.valueOf(blockName));
		dataHeaderRepository.save(dataHeaderEntity);
		
	}
}
