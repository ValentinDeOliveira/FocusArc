package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.controller.assertions.TagAssertion;
import com.valentin_d.focusarc.exception.tag.TagDoesNotExistException;
import com.valentin_d.focusarc.exception.tag.TagDoesNotExistForUserException;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.service.tag.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.TagFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagControllerTest extends BaseSecurityControllerTest {
    @MockitoBean
    private TagService tagService;
    private static final String ROOT = "/tags";
    private final TagAssertion tagAssertion = new TagAssertion();

    @Test
    void shouldReturnTag_whenIdExists() throws Exception {
        final var tag = aTagWithOwnerId(user.getId());
        when(tagService.findByIdAndOwnerId(eq(tag.getId()), eq(user.getId()))).thenReturn(Optional.of(tag));

        final var actions = mvcGetWithUser(tagUrl(tag.getId()), user)
                .andExpect(status().isOk());

        tagAssertion.assertSingleJson(actions, tag);
    }

    @Test
    void shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        when(tagService.findByIdAndOwnerId(any(TagId.class), eq(user.getId()))).thenReturn(Optional.empty());

        mvcGetWithUser(tagUrl(TagId.random()), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfTags_whenUserHasTags() throws Exception {
        final var tag = aTagWithOwnerId(user.getId());
        when(tagService.findAllForUser(user.getId())).thenReturn(List.of(tag));

        final var actions = mvcGetWithUser(ROOT + "/me", user)
                .andExpect(status().isOk());

        tagAssertion.assertListJson(actions, tag);
    }

    @Test
    void shouldReturnOk_whenUserHasNoTags() throws Exception {
        when(tagService.findAllForUser(user.getId())).thenReturn(List.of());

        mvcGetWithUser(ROOT + "/me", user)
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateTag_whenDataIsValid() throws Exception {
        final var tag = aTagWithOwnerId(user.getId());
        final var dto = aTagCreationDto();
        when(tagService.create(eq(user.getId()), eq(dto))).thenReturn(tag);

        final var actions = mvcPostWithUser(ROOT, toJson(dto), user)
                .andExpect(status().isCreated());

        tagAssertion.assertSingleJson(actions, tag);
    }

    @Test
    void shouldReturnTag_whenUpdatingExistingTag() throws Exception {
        final var tag = aTagWithOwnerId(user.getId());
        final var dto = aTagUpdateDto();
        when(tagService.update(eq(user.getId()), eq(tag.getId()), eq(dto))).thenReturn(tag);

        final var actions = mvcPutWithUser(tagUrl(tag.getId()), toJson(dto), user)
                .andExpect(status().isOk());

        tagAssertion.assertSingleJson(actions, tag);
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingTag() throws Exception {
        final var tagId = TagId.random();
        doThrow(new TagDoesNotExistException(tagId))
                .when(tagService).update(eq(user.getId()), eq(tagId), any());

        mvcPutWithUser(tagUrl(tagId), toJson(aTagUpdateDto()), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFound_whenUpdatingTagNotOwnedByUser() throws Exception {
        final var tagId = TagId.random();
        doThrow(new TagDoesNotExistForUserException())
                .when(tagService).update(eq(user.getId()), eq(tagId), any());

        mvcPutWithUser(tagUrl(tagId), toJson(aTagUpdateDto()), user)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNoContent_whenDeletingExistingTag() throws Exception {
        final var tag = aTagWithOwnerId(user.getId());

        mvcDeleteWithUser(tagUrl(tag.getId()), user)
                .andExpect(status().isNoContent());

        verify(tagService).delete(eq(user.getId()), eq(tag.getId()));
    }

    @Test
    void shouldReturnNoContent_whenDeletingAllTagsForUser() throws Exception {
        mvcDeleteWithUser(ROOT, user)
                .andExpect(status().isNoContent());

        verify(tagService).deleteAllForUser(eq(user.getId()));
    }

    private String tagUrl(final TagId tagId) {
        return ROOT + "/" + tagId.id();
    }
}