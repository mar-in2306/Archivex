package com.archivex.archivex.business.dto;

import com.archivex.archivex.business.enums.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long id;
    private String title;
    private String description;
    private DocumentStatus status;
    private String filePath;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long organizationId;
    private Long documentTypeId;
    private String documentTypeName;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
