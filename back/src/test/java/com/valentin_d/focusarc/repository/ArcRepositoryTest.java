package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerId;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class ArcRepositoryTest {
    @Autowired
    private ArcRepository arcRepository;

    @AfterEach
    void setUp() {
        arcRepository.deleteAll();
    }

    @Test
    void shouldReturnAllArcs_whenUserExists() {
        final UserId userId = UserId.random();

        final var arc1 = anArcWithOwnerId(userId);
        final var arc2 = anArcWithOwnerId(userId);
        arcRepository.save(arc1);
        arcRepository.save(arc2);

        final var arcsLists = arcRepository.findAllByOwner(userId);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(arc1, arc2);
    }
}