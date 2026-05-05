package com.valentin_d.focusarc.service.tag;

import com.valentin_d.focusarc.exception.tag.TagDoesNotExistException;
import com.valentin_d.focusarc.exception.tag.TagDoesNotExistForUserException;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.repository.TagRepository;
import com.valentin_d.focusarc.service.BaseLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TagLoader extends BaseLoader {
    private final TagRepository tagRepository;

    public Tag getTagIfExists(final TagId tagId) {
        return fetchOrThrow(tagRepository, tagId, () -> new TagDoesNotExistException(tagId));
    }

    public Optional<Tag> getTagByIdAndOwner(final TagId tagId, final UserId owner) {
        return tagRepository.findByIdAndOwner(tagId, owner);
    }

    public void assertOwnership(final Tag tag, final UserId userId) {
        if (!tag.getOwner().equals(userId)) {
            throw new TagDoesNotExistForUserException();
        }
    }

    public void assertTagsForUser(final UserId owner, final TagId tagId) {
        if (tagId == null) return;
        if (!tagRepository.existsByIdAndOwner(tagId, owner)) {
            throw new TagDoesNotExistForUserException();
        }
    }
}