package com.valentin_d.focusarc.service.tag;

import com.valentin_d.focusarc.dto.tag.TagCreationDto;
import com.valentin_d.focusarc.dto.tag.TagUpdateDto;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.repository.TagRepository;
import com.valentin_d.focusarc.service.user.UserLoader;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagLoader tagLoader;
    private final UserLoader userLoader;

    public Optional<Tag> findByIdAndOwnerId(@NotNull TagId tagId, @NotNull UserId ownerId) {
        return tagLoader.findTagByIdAndOwner(tagId, ownerId);
    }

    public List<Tag> getAllForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);
        return tagLoader.getAllByOwner(userId);
    }

    public Tag create(@NotNull UserId userId, @NotNull TagCreationDto dto) {
        userLoader.assertUserExists(userId);
        final var tag = new Tag(userId, dto.label(), dto.color());
        return tagRepository.save(tag);
    }

    public Tag update(@NotNull UserId userId, @NotNull TagId tagId, @NotNull TagUpdateDto dto) {
        final var tag = tagLoader.getTagIfExists(tagId);
        tagLoader.assertOwnership(tag, userId);

        if (dto.label() != null) tag.setLabel(dto.label());
        if (dto.color() != null) tag.setColor(dto.color());

        return tagRepository.save(tag);
    }

    public void delete(@NotNull UserId userId, @NotNull TagId tagId) {
        final var tag = tagLoader.getTagIfExists(tagId);
        tagLoader.assertOwnership(tag, userId);
        tagRepository.delete(tag);
    }

    public void deleteAllForUser(@NotNull UserId userId) {
        userLoader.assertUserExists(userId);

        final var tags = tagLoader.getAllByOwner(userId);
        tagRepository.deleteAll(tags);
    }
}