package org.epi_assist.EPIAssist_v2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${python.service.url:http://localhost:8000}")
    private String pythonServiceUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(pythonServiceUrl)
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }
}
