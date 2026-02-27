package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerIdAndStatus;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

        final var arc1 = anArcWithOwnerIdAndStatus(userId,  ArcStatus.COMPLETED);
        final var arc2 = anArcWithOwnerIdAndStatus(userId,  ArcStatus.COMPLETED);
        arcRepository.save(arc1);
        arcRepository.save(arc2);

        final var arcsLists = arcRepository.findAllByOwner(userId);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(arc1, arc2);
    }

    @Test
    void shouldReject_whenAnActiveArcForUserAlreadyExists() {
        final UserId userId = UserId.random();

        final var arc = anArcWithOwnerIdAndStatus(userId, ArcStatus.ACTIVE);
        final var duplicate = anArcWithOwnerIdAndStatus(userId, ArcStatus.ACTIVE);

        arcRepository.save(arc);

        assertThatThrownBy(() -> arcRepository.save(duplicate))
                .isInstanceOf(DuplicateKeyException.class);
    }

}