package com.archivex.archivex.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepDTO {
    private Long id;
    private String name;
    private String description;
    private Integer stepOrder;
    private Boolean isActive;
    private Long documentTypeId;
    private String documentTypeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
