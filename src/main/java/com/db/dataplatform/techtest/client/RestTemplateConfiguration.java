package com.db.dataplatform.techtest.client;

import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate createRestTemplate() {

        RestTemplate restTemplate = restTemplateBuilder.build();
        ObjectMapper objectMapper = new ObjectMapper();
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    	messageConverter.setPrettyPrint(false);
    	messageConverter.setObjectMapper(objectMapper);

        CloseableHttpClient client = HttpClients.createDefault();
       // RestTemplate template= new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
        restTemplate.setMessageConverters(Arrays.asList(messageConverter, new StringHttpMessageConverter()));

        return restTemplate;
    }

}
