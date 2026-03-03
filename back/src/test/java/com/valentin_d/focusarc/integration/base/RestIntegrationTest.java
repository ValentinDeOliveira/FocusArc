package com.valentin_d.focusarc.integration.base;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public abstract class RestIntegrationTest extends BaseIntegrationTest {
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final Object body, final Class<T> responseType) {
        var requestEntity = new HttpEntity<>(body);
        return restTemplate.exchange(buildUrl(url), method, requestEntity, responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final Class<T> responseType) {
        return restTemplate.exchange(buildUrl(url), method, HttpEntity.EMPTY, responseType);
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method,
                                            final HttpEntity<?> requestEntity ,final Class<T> responseType) {
        return restTemplate.exchange(buildUrl(url), method, requestEntity, responseType);
    }
}