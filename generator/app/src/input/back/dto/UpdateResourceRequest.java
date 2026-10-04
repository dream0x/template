package com.app.domain.resource.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UpdateResourceRequest {
  @NotBlank
  private String name;
}
