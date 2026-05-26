package com.archivex.archivex.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private String action;
    private String details;
    private String previousStatus;
    private String newStatus;
    private String ipAddress;
    private LocalDateTime performedAt;
    private Long documentId;
    private String documentTitle;
    private Long performedById;
    private String performedByName;
}
