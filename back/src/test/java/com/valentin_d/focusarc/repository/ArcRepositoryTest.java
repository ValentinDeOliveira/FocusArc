package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class ArcRepositoryTest {
    @Autowired
    private ArcRepository arcRepository;
    private static final UserId USER_ID = UserId.random();

    @Test
    void shouldReturnAllArcs_whenUserExists() {
        final var arc1 = new Arc(USER_ID, "Arc 1", 50);
        final var arc2 = new Arc(USER_ID, "Arc 2", 30);
        arcRepository.save(arc1);
        arcRepository.save(arc2);

        final var arcsLists = arcRepository.findAllByOwner(USER_ID);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(arc1, arc2);
    }

    @AfterEach
    void tearDown() {
        arcRepository.deleteAll();
    }
}