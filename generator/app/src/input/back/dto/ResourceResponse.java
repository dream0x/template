package com.app.domain.resource.dto;

import com.app.domain.resource.ResourceEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResourceResponse {
  @Schema(requiredMode = RequiredMode.REQUIRED)
  private Long id;
  @Schema(requiredMode = RequiredMode.REQUIRED)
  private String name;

  public static ResourceResponse from(ResourceEntity entity) {
    return ResourceResponse.builder()
        .id(entity.getId())
        .name(entity.getName())
        .build();
  }
}
