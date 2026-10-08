package com.app.domain.todo;

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
@RequestMapping("todos")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TodoEntity create(@Valid @RequestBody TodoRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<TodoEntity> findAll() {
    return service.findAll();
  }

  @GetMapping("{id}")
  public TodoEntity findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PutMapping("{id}")
  public TodoEntity update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
