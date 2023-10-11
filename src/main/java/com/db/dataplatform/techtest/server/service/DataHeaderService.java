package com.db.dataplatform.techtest.server.service;

import java.util.List;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

public interface DataHeaderService {
    void saveHeader(DataHeaderEntity entity);
    
    List<DataHeaderEntity> findByBlockType(BlockTypeEnum blockTypeEnum);
}
