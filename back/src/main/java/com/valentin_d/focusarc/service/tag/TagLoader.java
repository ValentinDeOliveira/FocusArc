package com.valentin_d.focusarc.service.tag;

import com.valentin_d.focusarc.exception.tag.TagDoesNotExistForUserException;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.TagRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TagLoader extends BaseService {
    private final TagRepository tagRepository;

    public void assertTagsForUser(UserId owner, Set<TagId> tagsId) {
        if (tagsId == null || tagsId.isEmpty()) return;
        long count = tagRepository.countByOwnerAndIdIn(owner, tagsId);
        if (count != tagsId.size()) {
            throw new TagDoesNotExistForUserException();
        }
    }
}