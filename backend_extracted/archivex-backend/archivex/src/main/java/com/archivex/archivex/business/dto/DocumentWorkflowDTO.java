package com.archivex.archivex.business.dto;

import com.archivex.archivex.business.enums.WorkflowTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentWorkflowDTO {
    private Long id;
    private WorkflowTaskStatus status;
    private String comments;
    private LocalDateTime completedAt;
    private Long documentId;
    private String documentTitle;
    private Long workflowStepId;
    private String workflowStepName;
    private Integer stepOrder;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
