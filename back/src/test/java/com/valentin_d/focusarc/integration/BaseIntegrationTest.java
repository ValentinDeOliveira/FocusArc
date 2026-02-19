package com.valentin_d.focusarc.integration;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @LocalServerPort
    private int port;
    protected final TestRestTemplate restTemplate = new TestRestTemplate();
    protected static final String BASE_URL = "http://localhost:";

    protected String buildUrl(final String path) {
        return BASE_URL + port + path;
    }

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method, final Object body, final Class<T> responseType) {
        var requestEntity = body != null ? new HttpEntity<>(body) : null;
        return restTemplate.exchange(buildUrl(url), method, requestEntity, responseType);
    }

    protected <T> void assertNotFound(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.NOT_FOUND, response);
    }

    protected <T> void assertNoContent(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.NO_CONTENT, response);
    }

    protected <T> void assertHasContent(final ResponseEntity<T> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private  <T> void assertResponseEmpty(final HttpStatus status,final ResponseEntity<T> response) {
        assertEquals(status, response.getStatusCode());
        assertNull(response.getBody());
    }
}