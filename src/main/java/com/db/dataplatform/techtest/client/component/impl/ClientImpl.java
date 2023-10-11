package com.db.dataplatform.techtest.client.component.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.db.dataplatform.techtest.client.RestTemplateConfiguration;
import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate(
			"http://localhost:8090/dataserver/update/{name}/{newBlockType}");

	@Autowired
	RestTemplateConfiguration restTemplateConfiguration;

	@Override
	public void pushData(DataEnvelope dataEnvelope) {

		HttpEntity<DataEnvelope> request = new HttpEntity<>(dataEnvelope);
		/**
		 * Calling the server controller with URI_PUSHDATA by passing the request object 
		 */		
		ResponseEntity<Boolean> value = restTemplateConfiguration.createRestTemplate().postForEntity(URI_PUSHDATA,
				request, Boolean.class);
		log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
	}

	/**
	 * This is a client call to hit the URI_GETDATA end point to get peristance data for a block type
	 *
	 */	
	@Override
	public List<DataEnvelope> getData(String blockType) {
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("blockType", blockType);
		String url = URI_GETDATA.expand(uriVariables).toString();
		RestTemplate restTemplate = new RestTemplate();
		List<DataEnvelope> dataEnvelopesList = new ArrayList<>();

		ResponseEntity<List> dataEnvelopes = restTemplate.exchange(url, HttpMethod.GET, null, List.class);
		dataEnvelopesList = dataEnvelopes.getBody();

		log.info("Query for data with header block type {}", blockType);
		return dataEnvelopesList;
	}

	/**
	 *  This is a client call to hit the URI_PATCHDATA end point to update block
	 * 
	 */
	@Override
	public boolean updateData(String blockName, String newBlockType) {
		log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("newBlockType", newBlockType);
		uriVariables.put("name", blockName);
		String url = URI_PATCHDATA.expand(uriVariables).toString();
		URI uri;
		Boolean response = false;

		try {
			uri = new URI(url);
			response = restTemplateConfiguration.createRestTemplate().getForObject(uri, Boolean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
