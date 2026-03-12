package com.valentin_d.focusarc.fixtures.tag;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.tag.Tag;
import com.valentin_d.focusarc.model.tag.TagColor;
import lombok.Builder;

@Builder
public class TagBuilder {
    @Builder.Default
    private final TagId id = TagId.random();
    @Builder.Default
    private final UserId owner = UserId.random();
    @Builder.Default
    private final String label = "My tag";
    @Builder.Default
    private final TagColor color = TagColor.BLUE;

    public Tag build() {
        return new Tag(id, owner, label, color);
    }
}
