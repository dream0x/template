package com.app.domain.tag;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TagMapper {

  int insert(TagEntity entity);

  List<TagEntity> findAll();

  TagEntity findById(Long id);

  List<TagEntity> findByTodoId(Long todoId);

  int countByIds(@Param("ids") List<Long> ids);

  int update(TagEntity entity);

  int delete(Long id);
}
