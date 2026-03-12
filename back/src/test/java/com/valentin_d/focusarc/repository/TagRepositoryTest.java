package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static com.valentin_d.focusarc.fixtures.factory.TagFactory.aTagWithOwnerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class TagRepositoryTest {
    @Autowired
    private TagRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllTagsForOwner_whenOwnerHasTags() {
        final var owner = UserId.random();
        final var tag1 = aTagWithOwnerId(owner);
        final var tag2 = aTagWithOwnerId(owner);
        repository.save(tag1);
        repository.save(tag2);

        final var result = repository.findAllByOwner(owner);

        assertThat(result).containsExactlyInAnyOrder(tag1, tag2);
    }

    @Test
    void shouldReturnEmpty_whenOwnerHasNoTags() {
        final var result = repository.findAllByOwner(UserId.random());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotReturnTagsOfOtherOwners_whenFindingByOwner() {
        final var owner = UserId.random();
        final var otherOwner = UserId.random();
        repository.save(aTagWithOwnerId(owner));
        repository.save(aTagWithOwnerId(otherOwner));

        final var result = repository.findAllByOwner(owner);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getOwner(), owner);
    }

    @Test
    void shouldReturnTag_whenIdAndOwnerMatch() {
        final var owner = UserId.random();
        final var tag = aTagWithOwnerId(owner);
        repository.save(tag);

        final var result = repository.findByIdAndOwner(tag.getId(), owner);

        assertThat(result).contains(tag);
    }

    @Test
    void shouldReturnEmpty_whenIdMatchesButOwnerDoesNot() {
        final var tag = aTagWithOwnerId(UserId.random());
        repository.save(tag);

        final var result = repository.findByIdAndOwner(tag.getId(), UserId.random());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmpty_whenIdDoesNotExist() {
        final var result = repository.findByIdAndOwner(TagId.random(), UserId.random());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnCorrectCount_whenAllTagsBelongToOwner() {
        final var owner = UserId.random();
        final var tag1 = aTagWithOwnerId(owner);
        final var tag2 = aTagWithOwnerId(owner);
        repository.save(tag1);
        repository.save(tag2);

        final long count = repository.countByOwnerAndIdIn(owner, Set.of(tag1.getId(), tag2.getId()));

        assertEquals(2, count);
    }

    @Test
    void shouldReturnPartialCount_whenSomeTagsDoNotBelongToOwner() {
        final var owner = UserId.random();
        final var tag = aTagWithOwnerId(owner);
        final var foreignTag = aTagWithOwnerId(UserId.random());
        repository.save(tag);
        repository.save(foreignTag);

        final long count = repository.countByOwnerAndIdIn(owner, Set.of(tag.getId(), foreignTag.getId()));

        assertEquals(1, count);
    }

    @Test
    void shouldReturnZero_whenNoTagsBelongToOwner() {
        final var owner = UserId.random();
        final var foreignTag = aTagWithOwnerId(UserId.random());
        repository.save(foreignTag);

        final long count = repository.countByOwnerAndIdIn(owner, Set.of(foreignTag.getId()));

        assertEquals(0, count);
    }
}