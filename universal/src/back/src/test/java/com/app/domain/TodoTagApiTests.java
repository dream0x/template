package com.app.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.domain.tag.TagEntity;
import com.app.domain.tag.TagMapper;
import com.app.domain.tag.TagRequest;
import com.app.domain.todo.TodoEntity;
import com.app.domain.todo.TodoMapper;
import com.app.domain.todo.TodoRequest;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

// 実DBを使い、各テストはロールバックされる。レスポンスとDBの両方を検証する
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoTagApiTests {

  @Autowired
  MockMvc mvc;
  @Autowired
  JsonMapper jsonMapper;
  @Autowired
  JdbcTemplate jdbc;
  @Autowired
  TagMapper tagMapper;
  @Autowired
  TodoMapper todoMapper;

  // ---- helpers ----

  private ResultActions sendJson(MockHttpServletRequestBuilder request, Object body) throws Exception {
    return mvc.perform(request
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonMapper.writeValueAsString(body)));
  }

  private <T> T read(ResultActions result, Class<T> type) throws Exception {
    return jsonMapper.readValue(result.andReturn().getResponse().getContentAsString(), type);
  }

  private long createTag(String name) throws Exception {
    TagEntity created = read(
        sendJson(post("/tags"), new TagRequest(name)).andExpect(status().isCreated()),
        TagEntity.class);
    assertThat(created.getName()).isEqualTo(name);
    return created.getId();
  }

  private long createTodo(String name, Long... tagIds) throws Exception {
    TodoEntity created = read(
        sendJson(post("/todos"), new TodoRequest(name, List.of(tagIds))).andExpect(status().isCreated()),
        TodoEntity.class);
    assertThat(created.getName()).isEqualTo(name);
    return created.getId();
  }

  private List<Long> tagIdsInDb(long todoId) {
    return jdbc.queryForList("SELECT tag_id FROM todo_tag WHERE todo_id = ? ORDER BY tag_id", Long.class, todoId);
  }

  private int count(String table) {
    return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
  }

  // ---- tag ----

  @Test
  void tagCrud() throws Exception {
    long id = createTag("t-crud");
    assertThat(tagMapper.findById(id).getName()).isEqualTo("t-crud");

    mvc.perform(get("/tags/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("t-crud"));

    sendJson(put("/tags/" + id), new TagRequest("t-crud2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("t-crud2"));
    assertThat(tagMapper.findById(id).getName()).isEqualTo("t-crud2");

    List<TagEntity> tags = jsonMapper.readValue(
        mvc.perform(get("/tags")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
        new TypeReference<List<TagEntity>>() {
        });
    assertThat(tags).filteredOn(t -> t.getId() == id).extracting(TagEntity::getName).containsExactly("t-crud2");

    mvc.perform(delete("/tags/" + id)).andExpect(status().isNoContent());
    assertThat(tagMapper.findById(id)).isNull();
    mvc.perform(get("/tags/" + id)).andExpect(status().isNotFound());
  }

  @Test
  void tagValidationAndErrors() throws Exception {
    int before = count("tag");
    sendJson(post("/tags"), new TagRequest("")).andExpect(status().isBadRequest());
    sendJson(post("/tags"), new TagRequest(null)).andExpect(status().isBadRequest());
    sendJson(post("/tags"), new TagRequest("x".repeat(256))).andExpect(status().isBadRequest());
    assertThat(count("tag")).isEqualTo(before);

    sendJson(put("/tags/-1"), new TagRequest("x")).andExpect(status().isNotFound());
    mvc.perform(delete("/tags/-1")).andExpect(status().isNotFound());
  }

  // 一意制約違反はPostgreSQLのトランザクションを中断させるため、ロールバック用トランザクション外で実行する
  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void duplicateTagNameIsRejected() throws Exception {
    long id = createTag("t-dup");
    try {
      sendJson(post("/tags"), new TagRequest("t-dup")).andExpect(status().isConflict());
      assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM tag WHERE name = 't-dup'", Integer.class)).isEqualTo(1);

      long other = createTag("t-dup-other");
      try {
        sendJson(put("/tags/" + other), new TagRequest("t-dup")).andExpect(status().isConflict());
        assertThat(tagMapper.findById(other).getName()).isEqualTo("t-dup-other");
      } finally {
        tagMapper.delete(other);
      }
    } finally {
      tagMapper.delete(id);
    }
  }

  // ---- todo ----

  @Test
  void todoCrudWithTags() throws Exception {
    long tag1 = createTag("t-a");
    long tag2 = createTag("t-b");
    long id = createTodo("todo1", tag1, tag2, tag1);

    // 重複したtagIdは1件にまとめられる
    assertThat(tagIdsInDb(id)).containsExactly(tag1, tag2);
    TodoEntity saved = todoMapper.findById(id);
    assertThat(saved.getName()).isEqualTo("todo1");
    assertThat(saved.getTags()).extracting(TagEntity::getId).containsExactly(tag1, tag2);

    mvc.perform(get("/todos/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("todo1"))
        .andExpect(jsonPath("$.tags", hasSize(2)))
        .andExpect(jsonPath("$.tags[0].name").value("t-a"))
        .andExpect(jsonPath("$.tags[1].name").value("t-b"));

    // 更新でタグが置き換わる
    sendJson(put("/todos/" + id), new TodoRequest("todo2", List.of(tag2)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("todo2"))
        .andExpect(jsonPath("$.tags", hasSize(1)))
        .andExpect(jsonPath("$.tags[0].name").value("t-b"));
    assertThat(todoMapper.findById(id).getName()).isEqualTo("todo2");
    assertThat(tagIdsInDb(id)).containsExactly(tag2);

    // 一覧にもタグが含まれる
    List<TodoEntity> todos = jsonMapper.readValue(
        mvc.perform(get("/todos")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString(),
        new TypeReference<List<TodoEntity>>() {
        });
    assertThat(todos).filteredOn(t -> t.getId() == id).singleElement().satisfies(t -> {
      assertThat(t.getName()).isEqualTo("todo2");
      assertThat(t.getTags()).extracting(TagEntity::getName).containsExactly("t-b");
    });

    // tagIdsを空にすると全て外れるが、タグ自体は残る
    sendJson(put("/todos/" + id), new TodoRequest("todo2", List.of()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tags", hasSize(0)));
    assertThat(tagIdsInDb(id)).isEmpty();
    assertThat(tagMapper.findById(tag2)).isNotNull();

    mvc.perform(delete("/todos/" + id)).andExpect(status().isNoContent());
    assertThat(todoMapper.findById(id)).isNull();
    mvc.perform(get("/todos/" + id)).andExpect(status().isNotFound());
  }

  @Test
  void todoWithoutTagIdsHasEmptyTags() throws Exception {
    TodoEntity created = read(
        sendJson(post("/todos"), Map.of("name", "no-tags"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.tags", hasSize(0))),
        TodoEntity.class);

    assertThat(todoMapper.findById(created.getId()).getName()).isEqualTo("no-tags");
    assertThat(tagIdsInDb(created.getId())).isEmpty();
  }

  @Test
  void todoErrors() throws Exception {
    int todosBefore = count("todo");
    int linksBefore = count("todo_tag");

    sendJson(post("/todos"), new TodoRequest("", List.of())).andExpect(status().isBadRequest());
    sendJson(post("/todos"), new TodoRequest("x", List.of(-1L))).andExpect(status().isBadRequest());
    sendJson(post("/todos"), new TodoRequest("x", Collections.singletonList(null))).andExpect(status().isBadRequest());

    // 不正なリクエストではDBに何も書き込まれない
    assertThat(count("todo")).isEqualTo(todosBefore);
    assertThat(count("todo_tag")).isEqualTo(linksBefore);

    sendJson(put("/todos/-1"), new TodoRequest("x", List.of())).andExpect(status().isNotFound());
    mvc.perform(get("/todos/-1")).andExpect(status().isNotFound());
    mvc.perform(delete("/todos/-1")).andExpect(status().isNotFound());
  }

  @Test
  void updateWithUnknownTagKeepsExistingTags() throws Exception {
    long tag = createTag("t-keep");
    long id = createTodo("todo-keep", tag);

    sendJson(put("/todos/" + id), new TodoRequest("changed", List.of(-1L)))
        .andExpect(status().isBadRequest());

    assertThat(todoMapper.findById(id).getName()).isEqualTo("todo-keep");
    assertThat(tagIdsInDb(id)).containsExactly(tag);
  }

  @Test
  void deletingTagDetachesFromTodo() throws Exception {
    long tag = createTag("t-detach");
    long id = createTodo("todo-detach", tag);
    assertThat(tagIdsInDb(id)).containsExactly(tag);

    mvc.perform(delete("/tags/" + tag)).andExpect(status().isNoContent());

    assertThat(tagMapper.findById(tag)).isNull();
    assertThat(tagIdsInDb(id)).isEmpty();
    assertThat(todoMapper.findById(id)).isNotNull();

    mvc.perform(get("/todos/" + id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tags", hasSize(0)));
  }

  @Test
  void deletingTodoRemovesLinksButKeepsTag() throws Exception {
    long tag = createTag("t-shared");
    long id = createTodo("todo-del", tag);

    mvc.perform(delete("/todos/" + id)).andExpect(status().isNoContent());

    assertThat(todoMapper.findById(id)).isNull();
    assertThat(tagIdsInDb(id)).isEmpty();
    assertThat(tagMapper.findById(tag)).isNotNull();
  }
}
