import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DocumentType } from '../models/document.model';

@Injectable({ providedIn: 'root' })
export class DocumentTypeService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/document-types`;

  listByOrganization(organizationId: number): Observable<DocumentType[]> {
    return this.http.get<DocumentType[]>(`${this.path}/organization/${organizationId}`);
  }

  getById(id: number, organizationId: number): Observable<DocumentType> {
    const params = new HttpParams().set('organizationId', organizationId);
    return this.http.get<DocumentType>(`${this.path}/${id}`, { params });
  }

  create(payload: DocumentType): Observable<DocumentType> {
    return this.http.post<DocumentType>(this.path, payload);
  }

  update(id: number, organizationId: number, payload: DocumentType): Observable<DocumentType> {
    const params = new HttpParams().set('organizationId', organizationId);
    return this.http.put<DocumentType>(`${this.path}/${id}`, payload, { params });
  }

  delete(id: number, organizationId: number): Observable<void> {
    const params = new HttpParams().set('organizationId', organizationId);
    return this.http.delete<void>(`${this.path}/${id}`, { params });
  }
}
