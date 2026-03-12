package com.valentin_d.focusarc.fixtures.factory;

import com.valentin_d.focusarc.dto.tag.TagCreationDto;
import com.valentin_d.focusarc.dto.tag.TagUpdateDto;
import com.valentin_d.focusarc.fixtures.tag.TagBuilder;
import com.valentin_d.focusarc.fixtures.tag.TagCreationDtoBuilder;
import com.valentin_d.focusarc.fixtures.tag.TagUpdateDtoBuilder;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;

public final class TagFactory {
    private TagFactory() {}

    public static Tag aTag() {
        return TagBuilder.builder().build().build();
    }

    public static Tag aTagWithOwnerId(final UserId owner) {
        return TagBuilder.builder().owner(owner).build().build();
    }

    public static TagCreationDto aTagCreationDto() {
        return TagCreationDtoBuilder.builder().build().build();
    }

    public static TagUpdateDto aTagUpdateDto() {
        return TagUpdateDtoBuilder.builder().build().build();
    }

    public static TagUpdateDto aTagUpdateDtoWithNullFields() {
        return new TagUpdateDto(null, null);
    }
}
