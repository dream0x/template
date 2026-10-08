package com.app.domain.todo;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TodoRequest(
    @NotBlank @Size(max = 255) String name,
    List<@NotNull Long> tagIds) {

  public TodoRequest {
    tagIds = tagIds == null ? List.of() : tagIds.stream().distinct().toList();
  }

  public TodoEntity toEntity() {
    TodoEntity entity = new TodoEntity();
    entity.setName(name);
    return entity;
  }
}
