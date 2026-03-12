package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.tag.TagUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseTagControllerIntegrationTest;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.tag.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.TagFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TagControllerIntegrationTest extends BaseTagControllerIntegrationTest {

    @Test
    void shouldCreateTag_whenDataIsValid() {
        final var dto = aTagCreationDto();

        final var response = request(URL, HttpMethod.POST, dto, Tag.class);

        assertionHelper.assertCreated(response);
        final var tag = response.getBody();
        assertNotNull(tag);
        assertNotNull(tag.getId());

        assertEquals(tag.getOwner(), user.getId());
        assertEquals(tag.getLabel(), dto.label());
        assertEquals(tag.getColor(), dto.color());
    }

    @Test
    void shouldReturnTag_whenIdExists() {
        final var tag = domainFixture.tagForUser(user.getId());

        final var response = request(tagUrl(tag.getId()), HttpMethod.GET, Tag.class);

        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());
        assertTagEquals(tag, response.getBody());
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExist() {
        final var response = request(tagUrl(TagId.random()), HttpMethod.GET, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenTagBelongsToAnotherUser() {
        final var otherUser = domainFixture.user();
        final var tag = domainFixture.tagForUser(otherUser.getId());

        final var response = request(tagUrl(tag.getId()), HttpMethod.GET, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnAllTags_whenUserHasTags() {
        final var tag1 = domainFixture.tagForUser(user.getId());
        final var tag2 = domainFixture.tagForUser(user.getId());

        final var response = request(URL + "/me", HttpMethod.GET, Tag[].class);

        assertionHelper.assertOk(response);
        assertThat(response.getBody()).containsExactlyInAnyOrder(tag1, tag2);
    }

    @Test
    void shouldReturnNoContent_whenUserHasNoTags() {
        final var response = request(URL + "/me", HttpMethod.GET, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideTagUpdateDtos")
    void shouldUpdateTag_withDifferentFields(final TagUpdateDto dto) {
        final var tag = domainFixture.tagForUser(user.getId());

        final var response = request(tagUrl(tag.getId()), HttpMethod.PUT, dto, Tag.class);

        assertionHelper.assertOk(response);
        final var result = response.getBody();

        assertNotNull(result);

        assertEquals(result.getId(), tag.getId());
        assertEquals(result.getOwner(), tag.getOwner());
        assertEquals(result.getLabel(), assertionHelper.expectedValue(dto.label(), tag.getLabel()));
        assertEquals(result.getColor(), assertionHelper.expectedValue(dto.color(), tag.getColor()));
    }

    private static Stream<Arguments> provideTagUpdateDtos() {
        return Stream.of(
                Arguments.of(aTagUpdateDto()),
                Arguments.of(aTagUpdateDtoWithNullFields())
        );
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingTag() {
        final var response = request(tagUrl(TagId.random()), HttpMethod.PUT, aTagUpdateDto(), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenUpdatingTagBelongingToAnotherUser() {
        final var otherUser = domainFixture.user();
        final var tag = domainFixture.tagForUser(otherUser.getId());

        final var response = request(tagUrl(tag.getId()), HttpMethod.PUT, aTagUpdateDto(), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteTag_whenIdExists() {
        final var tag = domainFixture.tagForUser(user.getId());

        final var response = request(tagUrl(tag.getId()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingTag() {
        final var response = request(tagUrl(TagId.random()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingTagBelongingToAnotherUser() {
        final var otherUser = domainFixture.user();
        final var tag = domainFixture.tagForUser(otherUser.getId());

        final var response = request(tagUrl(tag.getId()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteAllTagsForUser_whenUserHasTags() {
        domainFixture.tagForUser(user.getId());
        domainFixture.tagForUser(user.getId());

        final var response = request(URL, HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnBadRequest_whenCreatingTagWithBlankLabel() {
        final var response = request(URL, HttpMethod.POST,
                new com.valentin_d.focusarc.dto.tag.TagCreationDto("", com.valentin_d.focusarc.model.tag.TagColor.BLUE),
                Void.class);

        assertionHelper.assertBadRequest(response);
    }
}