package com.app.domain.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateResourceRequest {
  @NotBlank
  private String name;
}
