package com.app.domain.todo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {

  int insert(TodoEntity entity);

  List<TodoEntity> findAll();

  TodoEntity findById(Long id);

  int update(TodoEntity entity);

  int delete(Long id);

  int insertTags(@Param("todoId") Long todoId, @Param("tagIds") List<Long> tagIds);

  int deleteTags(@Param("todoId") Long todoId);
}
