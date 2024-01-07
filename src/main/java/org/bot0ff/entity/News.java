package org.bot0ff.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class News {
    Long id;
    String imgLink;
    String description;
}
