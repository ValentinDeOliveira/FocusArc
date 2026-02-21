package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.fixtures.arc.ArcBuilder;
import com.valentin_d.focusarc.fixtures.user.UserBuilder;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseArcControllerIntegrationTest extends BaseIntegrationTest{
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ArcRepository arcRepository;
    protected final String URL = "/arcs";

    @BeforeEach
    public void setUp() {
        arcRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected void assertArcEquals(final Arc expected, final Arc actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getOwner(), actual.getOwner());
        assertEquals(expected.getTotalPlannedMinutes(), actual.getTotalPlannedMinutes());
        assertEquals(expected.getTotalCompletedMinutes(), actual.getTotalCompletedMinutes());
    }

    protected User createUser() {
        final var user = UserBuilder.builder().build().build();
        return userRepository.save(user);
    }

    protected Arc createArc() {
        final var user = createUser();
        return createArcForUser(user.getId());
    }

    protected Arc createArcForUser(UserId ownerId) {
        final var arc = ArcBuilder.builder()
                .owner(ownerId)
                .build()
                .build();
        return arcRepository.save(arc);
    }
}