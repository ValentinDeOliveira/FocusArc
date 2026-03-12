package com.valentin_d.focusarc.fixtures.tag;

import com.valentin_d.focusarc.dto.tag.TagCreationDto;
import com.valentin_d.focusarc.model.tag.TagColor;
import lombok.Builder;

@Builder
public class TagCreationDtoBuilder {
    @Builder.Default
    private final String label = "My tag";
    @Builder.Default
    private final TagColor color = TagColor.BLUE;

    public TagCreationDto build() {
        return new TagCreationDto(label, color);
    }
}
