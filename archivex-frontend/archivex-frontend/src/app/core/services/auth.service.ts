import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { User } from '../models/user.model';
import { UserRole } from '../models/role.model';
import { environment } from '../../../environments/environment';

interface LoginRequest {
  subdomain?: string;
  email: string;
  password: string;
}

/** Forma EXACTA que devuelve tu backend en POST /api/v1/auth/login */
interface LoginResponseDTO {
  token: string;
  tokenType: string;
  userId: number;
  userName: string;
  userEmail: string;
  role: UserRole;
  organizationId: number;
  organizationName: string;
}

const TOKEN_KEY = 'archivex.token';
const USER_KEY = 'archivex.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  private readonly _currentUser = signal<User | null>(this.loadStoredUser());
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  login(payload: LoginRequest): Observable<User> {
    return this.http
      .post<LoginResponseDTO>(`${environment.apiUrl}/auth/login`, {
        email: payload.email,
        password: payload.password,
      })
      .pipe(
        tap((res) => this.persistSession(res)),
        map(() => this._currentUser()!),
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._currentUser.set(null);
  }

  requestPasswordReset(email: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/password/forgot`, {
      email,
    });
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/password/reset`, {
      token,
      newPassword,
    });
  }

  getAccessToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private persistSession(res: LoginResponseDTO): void {
    const user: User = {
      id: res.userId,
      organizationId: res.organizationId,
      email: res.userEmail,
      name: res.userName,
      role: res.role,
      isActive: true,
      organizationName: res.organizationName,
    };
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this._currentUser.set(user);
  }

  private loadStoredUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }
}
