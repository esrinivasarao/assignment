package com.db.dataplatform.techtest.server.api.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.db.dataplatform.techtest.client.RestTemplateConfiguration;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/dataserver")
@RequiredArgsConstructor
@Validated
public class ServerController {

	public static final String URI_PUSHDATA = "http://localhost:8090/hadoopserver/pushbigdata";
	private final Server server;
	
	private final RestTemplateConfiguration restTemplateConfiguration;

	@PostMapping(value = "/pushdata", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> pushData(@Valid @RequestBody DataEnvelope dataEnvelope)
			throws IOException, NoSuchAlgorithmException, Exception {

		log.info("Data envelope received: {}", dataEnvelope.getDataHeader().getName());
		boolean checksumPass = server.saveDataEnvelope(dataEnvelope);
		if(checksumPass) {
//			HttpStatus hadoopResponse = pushDataToHadoop(dataEnvelope);
//			log.info("Push data to hadoop server. Response: {}", hadoopResponse);
		}

		log.info("Data envelope persisted. Attribute name: {}", dataEnvelope.getDataHeader().getName());
		
		return ResponseEntity.ok(checksumPass);
	}

	@RequestMapping(value = "/data/{blockType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DataEnvelope>> getBlockData(@PathVariable("blockType") String blockType)
			throws IOException, NoSuchAlgorithmException {
		List<DataEnvelope> dataEnvelopes = server.getBlockDataByBlockType(blockType);
		return ResponseEntity.ok(dataEnvelopes);
	}

	@RequestMapping(value = "/update/{name}/{newBlockType}",  method = RequestMethod.GET)
	public ResponseEntity<Boolean> updateBlockData(@PathVariable("name")  @Valid String name,
			@PathVariable("newBlockType")  @Valid String newBlockType) throws IOException, NoSuchAlgorithmException {
		 server.updateBlockType(name, newBlockType);
		return ResponseEntity.ok(true);
	}
	
	@Retryable(maxAttempts = 3, value = HttpServerErrorException.class, backoff = @Backoff(delay = 3000))
	private HttpStatus pushDataToHadoop(DataEnvelope dataEnvelope) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setConnectTimeout(5000);
		requestFactory.setReadTimeout(10000);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpEntity<String> request = new HttpEntity<>(dataEnvelope.toString());
		ResponseEntity<HttpStatus> response = restTemplate.postForEntity(URI_PUSHDATA,
				request, HttpStatus.class);
		return response.getStatusCode();
	}

}
