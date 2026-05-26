export enum DocumentStatus {
  CREATED = 'CREATED',
  UNDER_REVIEW = 'UNDER_REVIEW',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
}

export enum WorkflowTaskStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  REJECTED = 'REJECTED',
}

export interface Document {
  id?: number;
  title: string;
  description?: string;
  status?: DocumentStatus;
  filePath?: string;
  fileName?: string;
  fileType?: string;
  fileSize?: number;
  organizationId?: number;
  documentTypeId?: number;
  documentTypeName?: string;
  createdById?: number;
  createdByName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DocumentType {
  id?: number;
  name: string;
  description?: string;
  isActive?: boolean;
  organizationId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface WorkflowStep {
  id?: number;
  name: string;
  description?: string;
  stepOrder: number;
  isActive?: boolean;
  documentTypeId: number;
  documentTypeName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DocumentWorkflow {
  id?: number;
  status?: WorkflowTaskStatus;
  comments?: string;
  completedAt?: string;
  documentId: number;
  documentTitle?: string;
  workflowStepId: number;
  workflowStepName?: string;
  stepOrder?: number;
  assignedToId: number;
  assignedToName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface EmailTemplate {
  id?: number;
  eventType: string;
  subject: string;
  bodyHtml: string;
  isActive?: boolean;
  organizationId?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AuditLog {
  id?: number;
  action: string;
  details?: string;
  previousStatus?: string;
  newStatus?: string;
  ipAddress?: string;
  performedAt?: string;
  documentId?: number;
  documentTitle?: string;
  performedById?: number;
  performedByName?: string;
}
