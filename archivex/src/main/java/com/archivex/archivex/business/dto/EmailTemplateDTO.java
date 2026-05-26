package com.archivex.archivex.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplateDTO {
    private Long id;
    private String eventType;
    private String subject;
    private String bodyHtml;
    private Boolean isActive;
    private Long organizationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
