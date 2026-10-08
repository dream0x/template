package com.app.domain.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
    @NotBlank @Size(max = 255) String name) {

  public TagEntity toEntity() {
    TagEntity entity = new TagEntity();
    entity.setName(name);
    return entity;
  }
}
