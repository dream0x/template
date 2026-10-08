package com.app.domain.tag;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

  private final TagMapper mapper;

  @Transactional
  public TagEntity create(TagRequest request) {
    TagEntity entity = request.toEntity();
    try {
      mapper.insert(entity);
    } catch (DuplicateKeyException e) {
      throw duplicated(request.name());
    }
    return entity;
  }

  public List<TagEntity> findAll() {
    return mapper.findAll();
  }

  public TagEntity findById(Long id) {
    TagEntity entity = mapper.findById(id);
    if (entity == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "tag not found: id=" + id);
    }
    return entity;
  }

  @Transactional
  public TagEntity update(Long id, TagRequest request) {
    TagEntity entity = request.toEntity();
    entity.setId(id);
    int updated;
    try {
      updated = mapper.update(entity);
    } catch (DuplicateKeyException e) {
      throw duplicated(request.name());
    }
    if (updated == 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "tag not found: id=" + id);
    }
    return entity;
  }

  @Transactional
  public void delete(Long id) {
    if (mapper.delete(id) == 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "tag not found: id=" + id);
    }
  }

  private ResponseStatusException duplicated(String name) {
    return new ResponseStatusException(HttpStatus.CONFLICT, "tag already exists: name=" + name);
  }
}
