package com.app.domain.tag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TagEntity create(@Valid @RequestBody TagRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<TagEntity> findAll() {
    return service.findAll();
  }

  @GetMapping("{id}")
  public TagEntity findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PutMapping("{id}")
  public TagEntity update(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
