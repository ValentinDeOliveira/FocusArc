package com.valentin_d.focusarc.integration;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @LocalServerPort
    private static int PORT;
    protected final TestRestTemplate restTemplate = new TestRestTemplate();
    protected static final String BASE_URL = "http://localhost:" + PORT;

    protected <T> ResponseEntity<T> request(final String url, final HttpMethod method, final Object body, final Class<T> responseType) {
        var requestEntity = body != null ? new HttpEntity<>(body) : null;
        return restTemplate.exchange(url, method, requestEntity, responseType);
    }

    protected <T> void assertNotFound(final ResponseEntity<T> response) {
        assertEquals(HttpStatus.NOT_FOUND, response);
        assertNull(response.getBody());
    }
}