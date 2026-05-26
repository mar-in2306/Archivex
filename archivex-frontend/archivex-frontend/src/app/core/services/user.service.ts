import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private readonly path = `${environment.apiUrl}/users`;

  listByOrganization(organizationId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.path}/organization/${organizationId}`);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.path}/${id}`);
  }

  create(payload: User): Observable<User> {
    return this.http.post<User>(this.path, payload);
  }

  update(id: number, payload: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.path}/${id}`, payload);
  }

  toggleStatus(id: number): Observable<User> {
    return this.http.patch<User>(`${this.path}/${id}/toggle-status`, null);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.path}/${id}`);
  }
}
