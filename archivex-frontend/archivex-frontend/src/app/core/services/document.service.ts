import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Document, DocumentStatus } from '../models/document.model';

export interface DocumentFilters {
  status?: DocumentStatus | '';
  documentTypeId?: number | null;
  from?: string;
  to?: string;
}

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/documents`;

  list(): Observable<Document[]> {
    return this.http.get<Document[]>(this.path);
  }

  filter(filters: DocumentFilters): Observable<Document[]> {
    let params = new HttpParams();
    if (filters.status) params = params.set('status', filters.status);
    if (filters.documentTypeId) params = params.set('documentTypeId', filters.documentTypeId);
    if (filters.from) params = params.set('from', filters.from);
    if (filters.to) params = params.set('to', filters.to);
    return this.http.get<Document[]>(`${this.path}/filter`, { params });
  }

  getById(id: number): Observable<Document> {
    return this.http.get<Document>(`${this.path}/${id}`);
  }

  create(document: Document, file?: File | null): Observable<Document> {
    const form = new FormData();
    form.append(
      'document',
      new Blob([JSON.stringify(document)], { type: 'application/json' }),
    );
    if (file) form.append('file', file);
    return this.http.post<Document>(this.path, form);
  }

  updateMetadata(id: number, payload: Document): Observable<Document> {
    return this.http.put<Document>(`${this.path}/${id}/metadata`, payload);
  }

  changeStatus(id: number, newStatus: DocumentStatus): Observable<Document> {
    const params = new HttpParams().set('newStatus', newStatus);
    return this.http.patch<Document>(`${this.path}/${id}/status`, null, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${id}`);
  }

  download(id: number): Observable<Blob> {
    return this.http.get(`${this.path}/${id}/download`, { responseType: 'blob' });
  }
}
