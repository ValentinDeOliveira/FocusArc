package com.valentin_d.focusarc.integration.base;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    protected IntegrationAssertionHelper assertionHelper = new IntegrationAssertionHelper();

    @LocalServerPort
    private int port;
    private static final String BASE_URL = "http://localhost:";

    protected String buildUrl(final String path) {
        return BASE_URL + port + path;
    }
}