package com.valentin_d.focusarc.fixtures.tag;

import com.valentin_d.focusarc.dto.tag.TagUpdateDto;
import com.valentin_d.focusarc.model.tag.TagColor;
import lombok.Builder;

@Builder
public class TagUpdateDtoBuilder {
    @Builder.Default
    private final String label = "Updated tag";
    @Builder.Default
    private final TagColor color = TagColor.RED;

    public TagUpdateDto build() {
        return new TagUpdateDto(label, color);
    }
}
