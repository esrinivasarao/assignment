package com.db.dataplatform.techtest.server.component.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import com.db.dataplatform.techtest.TechTestApplication;
import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

	private final DataBodyService dataBodyServiceImpl;
	private final ModelMapper modelMapper;

	/**
	 * @param envelope
	 * @return true if there is a match with the client provided checksum.
	 * @throws NoSuchAlgorithmException
	 */
	@Override
	public boolean saveDataEnvelope(DataEnvelope envelope) throws NoSuchAlgorithmException {

		if (checksum(envelope)) {
			persist(envelope);

			log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
			return true;
		} else {
			log.info("Checksum not matched, data name: {}", envelope.getDataHeader().getName());
			return false;
		}

	}

	private void persist(DataEnvelope envelope) {
		log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
		DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

		DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
		dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

		saveData(dataBodyEntity);
	}

	private void saveData(DataBodyEntity dataBodyEntity) {
		dataBodyServiceImpl.saveDataBody(dataBodyEntity);
	}

	@Override
	public List<DataEnvelope> getBlockDataByBlockType(String blockType) {
		List<DataBodyEntity> dataBodyEntities = dataBodyServiceImpl
				.getDataByBlockType(BlockTypeEnum.valueOf(blockType));
		List<DataEnvelope> dataEnvelopes = new ArrayList<>();
		for (Iterator iterator = dataBodyEntities.iterator(); iterator.hasNext();) {
			DataBodyEntity dataBodyEntity = (DataBodyEntity) iterator.next();
			DataEnvelope dataEnvelope = new DataEnvelope();
			DataBody dataBody = new DataBody();
			dataBody.setDataBody(dataBodyEntity.getDataBody());
			DataHeader dataHeader = new DataHeader(dataBodyEntity.getDataHeaderEntity().getName(),
					dataBodyEntity.getDataHeaderEntity().getBlocktype());
			dataEnvelope.setDataBody(dataBody);
			dataEnvelope.setDataHeader(dataHeader);
			dataEnvelopes.add(dataEnvelope);

		}
		return dataEnvelopes;
	}

	@Override
	public void updateBlockType(String name, String blockName) {
		dataBodyServiceImpl.updateBlockName(name, blockName);
	}

	private Boolean checksum(DataEnvelope envelope) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] data = SerializationUtils.serialize(envelope.getDataBody().getDataBody());
		md.update(data);

		byte[] md5Checksum = md.digest();

		StringBuilder hexString = new StringBuilder();
		for (byte b : md5Checksum) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		System.out.println(TechTestApplication.MD5_CHECKSUM);
		System.out.println(hexString);
		if (TechTestApplication.MD5_CHECKSUM.equals(hexString.toString())) {

			return true;
		} else {
			return false;
		}
	}

}
