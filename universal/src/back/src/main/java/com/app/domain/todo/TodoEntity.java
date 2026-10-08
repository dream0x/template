package com.app.domain.todo;

import java.util.ArrayList;
import java.util.List;

import com.app.domain.tag.TagEntity;

import lombok.Data;

@Data
public class TodoEntity {
  private Long id;
  private String name;
  private List<TagEntity> tags = new ArrayList<>();
}
