import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Organization } from '../models/organization.model';
import { User } from '../models/user.model';

export interface RegisterOrganizationPayload {
  orgName: string;
  orgDomain: string;
  orgContactEmail: string;
  orgDescription?: string;
  adminName: string;
  adminEmail: string;
  adminPassword: string;
}

@Injectable({ providedIn: 'root' })
export class OrganizationService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/organizations`;

  list(): Observable<Organization[]> {
    return this.http.get<Organization[]>(this.path);
  }

  getById(id: number): Observable<Organization> {
    return this.http.get<Organization>(`${this.path}/${id}`);
  }

  register(payload: RegisterOrganizationPayload): Observable<User> {
    let params = new HttpParams();
    for (const [key, value] of Object.entries(payload)) {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, String(value));
      }
    }
    return this.http.post<User>(`${this.path}/register`, null, { params });
  }

  create(payload: Organization): Observable<Organization> {
    return this.http.post<Organization>(this.path, payload);
  }

  update(id: number, payload: Organization): Observable<Organization> {
    return this.http.put<Organization>(`${this.path}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${id}`);
  }
}
