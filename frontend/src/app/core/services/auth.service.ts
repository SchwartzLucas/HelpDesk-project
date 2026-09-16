import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginResponse } from '../models';
import { tap } from 'rxjs';

const TOKEN_KEY = 'hd_token';
const USER_KEY = 'hd_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private userSignal = signal<LoginResponse | null>(null);

  readonly user = this.userSignal.asReadonly();
  readonly isLoggedIn = computed(() => this.userSignal() !== null);
  readonly isAdmin = computed(() => {
    const u = this.userSignal();
    return !!u && (u.role === 'ADMIN_USER' || u.role === 'SUPPORT_USER');
  });

  constructor(private http: HttpClient) {
    const raw = localStorage.getItem(USER_KEY);
    if (raw) {
      try {
        this.userSignal.set(JSON.parse(raw));
      } catch {
        localStorage.removeItem(USER_KEY);
      }
    }
  }

  get token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  login(login: string, password: string) {
    return this.http
      .post<LoginResponse>('/auth/login', { login, password })
      .pipe(
        tap((r) => {
          localStorage.setItem(TOKEN_KEY, r.token);
          localStorage.setItem(USER_KEY, JSON.stringify(r));
          this.userSignal.set(r);
        }),
      );
  }

  register(payload: {
    name: string;
    login: string;
    password: string;
    role: string;
    teamId?: number | null;
    clientId?: number | null;
  }) {
    return this.http.post('/auth/register', payload);
  }

  logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSignal.set(null);
  }
}