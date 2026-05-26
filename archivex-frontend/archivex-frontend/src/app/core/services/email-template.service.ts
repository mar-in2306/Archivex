import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EmailTemplate } from '../models/document.model';

@Injectable({ providedIn: 'root' })
export class EmailTemplateService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/email-templates`;

  list(): Observable<EmailTemplate[]> {
    return this.http.get<EmailTemplate[]>(this.path);
  }

  getById(id: number): Observable<EmailTemplate> {
    return this.http.get<EmailTemplate>(`${this.path}/${id}`);
  }

  create(payload: EmailTemplate): Observable<EmailTemplate> {
    return this.http.post<EmailTemplate>(this.path, payload);
  }

  update(id: number, payload: EmailTemplate): Observable<EmailTemplate> {
    return this.http.put<EmailTemplate>(`${this.path}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${id}`);
  }
}
