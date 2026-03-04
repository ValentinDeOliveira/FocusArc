package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.fixtures.DomainFixture;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    protected final IntegrationAssertionHelper assertionHelper = new IntegrationAssertionHelper();
    @LocalServerPort
    private int port;
    private static final String BASE_URL = "http://localhost:";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    protected DomainFixture domainFixture;

    protected String buildUrl(final String path) {
        return BASE_URL + port + path;
    }

    @BeforeEach
    void reset() {
        mongoTemplate.getDb().drop();
    }
}