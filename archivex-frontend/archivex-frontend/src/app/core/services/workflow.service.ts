import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DocumentWorkflow, WorkflowStep, WorkflowTaskStatus } from '../models/document.model';

@Injectable({ providedIn: 'root' })
export class WorkflowService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/workflow`;

  createStep(payload: WorkflowStep): Observable<WorkflowStep> {
    return this.http.post<WorkflowStep>(`${this.path}/steps`, payload);
  }

  listSteps(documentTypeId: number): Observable<WorkflowStep[]> {
    return this.http.get<WorkflowStep[]>(`${this.path}/steps/document-type/${documentTypeId}`);
  }

  updateStep(id: number, payload: WorkflowStep): Observable<WorkflowStep> {
    return this.http.put<WorkflowStep>(`${this.path}/steps/${id}`, payload);
  }

  deleteStep(id: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/steps/${id}`);
  }

  assignTask(payload: DocumentWorkflow): Observable<DocumentWorkflow> {
    return this.http.post<DocumentWorkflow>(`${this.path}/tasks`, payload);
  }

  listTasksByDocument(documentId: number): Observable<DocumentWorkflow[]> {
    return this.http.get<DocumentWorkflow[]>(`${this.path}/tasks/document/${documentId}`);
  }

  listPendingTasks(): Observable<DocumentWorkflow[]> {
    return this.http.get<DocumentWorkflow[]>(`${this.path}/tasks/pending`);
  }

  resolveTask(taskId: number, newStatus: WorkflowTaskStatus, comments = ''): Observable<DocumentWorkflow> {
    const params = new HttpParams().set('newStatus', newStatus);
    return this.http.patch<DocumentWorkflow>(`${this.path}/tasks/${taskId}/resolve`, { comments }, { params });
  }
}
