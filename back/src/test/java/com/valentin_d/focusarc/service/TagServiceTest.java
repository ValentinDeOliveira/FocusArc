package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.tag.TagDoesNotExistException;
import com.valentin_d.focusarc.exception.tag.TagDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.user.UserDoesNotExistException;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.repository.TagRepository;
import com.valentin_d.focusarc.service.tag.TagLoader;
import com.valentin_d.focusarc.service.tag.TagService;
import com.valentin_d.focusarc.service.user.UserLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.TagFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagLoader tagLoader;
    @Mock
    private UserLoader userLoader;
    @InjectMocks
    private TagService tagService;

    @Test
    void shouldCreateTag_whenUserExists() {
        final var user = aUser();
        final var dto = aTagCreationDto();
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        final var result = tagService.create(user.getId(), dto);

        assertEquals(result.getOwner(), user.getId());
        assertEquals(result.getLabel(), dto.label());
        assertEquals(result.getColor(), dto.color());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void shouldThrowExceptionOnCreate_whenUserDoesNotExist() {
        final var userId = UserId.random();
        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> tagService.create(userId, aTagCreationDto()))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldReturnTag_whenIdAndOwnerMatch() {
        final var tag = aTag();
        when(tagLoader.findTagByIdAndOwner(tag.getId(), tag.getOwner())).thenReturn(Optional.of(tag));

        final var result = tagService.findByIdAndOwnerId(tag.getId(), tag.getOwner());

        assertThat(result).contains(tag);
    }

    @Test
    void shouldReturnEmpty_whenIdOrOwnerDoNotMatch() {
        when(tagLoader.findTagByIdAndOwner(any(), any())).thenReturn(Optional.empty());

        final var result = tagService.findByIdAndOwnerId(TagId.random(), UserId.random());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnAllTags_whenUserExists() {
        final var user = aUser();
        final var tag = aTagWithOwnerId(user.getId());
        when(tagLoader.getAllByOwner(user.getId())).thenReturn(List.of(tag));

        final var result = tagService.getAllForUser(user.getId());

        assertThat(result).containsExactly(tag);
    }

    @Test
    void shouldThrowExceptionOnFindAll_whenUserDoesNotExist() {
        final var userId = UserId.random();
        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> tagService.getAllForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(tagRepository, never()).findAllByOwner(any());
    }

    @Test
    void shouldUpdateTag_whenTagExistsAndUserOwnsIt() {
        final var user = aUser();
        final var tag = aTagWithOwnerId(user.getId());
        final var dto = aTagUpdateDto();
        when(tagLoader.getTagIfExists(tag.getId())).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        final var result = tagService.update(user.getId(), tag.getId(), dto);

        assertEquals(result.getLabel(), dto.label());
        assertEquals(result.getColor(), dto.color());
        verify(tagRepository).save(tag);
    }

    @Test
    void shouldNotOverwriteNullFields_whenUpdatingTag() {
        final var user = aUser();
        final var tag = aTagWithOwnerId(user.getId());
        final var originalLabel = tag.getLabel();
        final var originalColor = tag.getColor();
        when(tagLoader.getTagIfExists(tag.getId())).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        final var result = tagService.update(user.getId(), tag.getId(), aTagUpdateDtoWithNullFields());

        assertEquals(result.getLabel(), originalLabel);
        assertEquals(result.getColor(), originalColor);
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenTagDoesNotExist() {
        final var tagId = TagId.random();
        when(tagLoader.getTagIfExists(tagId)).thenThrow(new TagDoesNotExistException(tagId));

        assertThatThrownBy(() -> tagService.update(UserId.random(), tagId, aTagUpdateDto()))
                .isInstanceOf(TagDoesNotExistException.class);

        verify(tagRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenUserDoesNotOwnTag() {
        final var owner = aUser();
        final var attacker = aUser();
        final var tag = aTagWithOwnerId(owner.getId());
        when(tagLoader.getTagIfExists(tag.getId())).thenReturn(tag);
        doThrow(new TagDoesNotExistForUserException())
                .when(tagLoader).assertOwnership(eq(tag), eq(attacker.getId()));

        assertThatThrownBy(() -> tagService.update(attacker.getId(), tag.getId(), aTagUpdateDto()))
                .isInstanceOf(TagDoesNotExistForUserException.class);

        verify(tagRepository, never()).save(any());
    }

    @Test
    void shouldDeleteTag_whenTagExistsAndUserOwnsIt() {
        final var user = aUser();
        final var tag = aTagWithOwnerId(user.getId());
        when(tagLoader.getTagIfExists(tag.getId())).thenReturn(tag);

        tagService.delete(user.getId(), tag.getId());

        verify(tagRepository).delete(tag);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenTagDoesNotExist() {
        final var tagId = TagId.random();
        when(tagLoader.getTagIfExists(tagId)).thenThrow(new TagDoesNotExistException(tagId));

        assertThatThrownBy(() -> tagService.delete(UserId.random(), tagId))
                .isInstanceOf(TagDoesNotExistException.class);

        verify(tagRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionOnDelete_whenUserDoesNotOwnTag() {
        final var owner = aUser();
        final var attacker = aUser();
        final var tag = aTagWithOwnerId(owner.getId());
        when(tagLoader.getTagIfExists(tag.getId())).thenReturn(tag);
        doThrow(new TagDoesNotExistForUserException())
                .when(tagLoader).assertOwnership(eq(tag), eq(attacker.getId()));

        assertThatThrownBy(() -> tagService.delete(attacker.getId(), tag.getId()))
                .isInstanceOf(TagDoesNotExistForUserException.class);

        verify(tagRepository, never()).delete(any());
    }

    @Test
    void shouldDeleteAllTags_whenUserExists() {
        final var user = aUser();
        final var tag = aTagWithOwnerId(user.getId());
        when(tagLoader.getAllByOwner(user.getId())).thenReturn(List.of(tag));

        tagService.deleteAllForUser(user.getId());

        verify(tagRepository).deleteAll(List.of(tag));
    }

    @Test
    void shouldThrowExceptionOnDeleteAll_whenUserDoesNotExist() {
        final var userId = UserId.random();
        doThrowUserDoesNotExist(userId);

        assertThatThrownBy(() -> tagService.deleteAllForUser(userId))
                .isInstanceOf(UserDoesNotExistException.class);

        verify(tagRepository, never()).deleteAll(anyList());
    }

    private void doThrowUserDoesNotExist(final UserId userId) {
        doThrow(new UserDoesNotExistException(userId))
                .when(userLoader).assertUserExists(eq(userId));
    }
}