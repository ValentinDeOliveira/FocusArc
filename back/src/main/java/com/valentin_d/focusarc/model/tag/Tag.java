package com.valentin_d.focusarc.model.tag;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("tags")
public class Tag {
    @Id
    private TagId id;
    private UserId owner;
    private String label;
    private TagColor color;
}