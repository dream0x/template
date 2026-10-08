package com.app.domain.todo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.app.domain.tag.TagMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

  private final TodoMapper mapper;
  private final TagMapper tagMapper;

  @Transactional
  public TodoEntity create(TodoRequest request) {
    validateTagIds(request.tagIds());
    TodoEntity entity = request.toEntity();
    mapper.insert(entity);
    replaceTags(entity.getId(), request.tagIds());
    return mapper.findById(entity.getId());
  }

  public List<TodoEntity> findAll() {
    return mapper.findAll();
  }

  public TodoEntity findById(Long id) {
    TodoEntity entity = mapper.findById(id);
    if (entity == null) {
      throw notFound(id);
    }
    return entity;
  }

  @Transactional
  public TodoEntity update(Long id, TodoRequest request) {
    validateTagIds(request.tagIds());
    TodoEntity entity = request.toEntity();
    entity.setId(id);
    if (mapper.update(entity) == 0) {
      throw notFound(id);
    }
    replaceTags(id, request.tagIds());
    return mapper.findById(id);
  }

  @Transactional
  public void delete(Long id) {
    if (mapper.delete(id) == 0) {
      throw notFound(id);
    }
  }

  private void validateTagIds(List<Long> tagIds) {
    if (!tagIds.isEmpty() && tagMapper.countByIds(tagIds) != tagIds.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown tagIds: " + tagIds);
    }
  }

  private void replaceTags(Long todoId, List<Long> tagIds) {
    mapper.deleteTags(todoId);
    if (!tagIds.isEmpty()) {
      mapper.insertTags(todoId, tagIds);
    }
  }

  private ResponseStatusException notFound(Long id) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, "todo not found: id=" + id);
  }
}
