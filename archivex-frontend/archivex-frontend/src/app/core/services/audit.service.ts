import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuditLog } from '../models/document.model';

@Injectable({ providedIn: 'root' })
export class AuditService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/audit`;

  byOrganization(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.path}/organization`);
  }

  byDocument(documentId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.path}/document/${documentId}`);
  }

  byUser(userId: number): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>(`${this.path}/user/${userId}`);
  }
}
