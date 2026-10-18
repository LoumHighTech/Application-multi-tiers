import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { Role, Session } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly key = 'projets-tutores-session';
  private readonly current = signal<Session | null>(this.readSession());
  readonly session = this.current.asReadonly();
  readonly isAuthenticated = computed(() => !!this.current()?.token);
  constructor(private http: HttpClient, private router: Router) {}
  login(email: string, motDePasse: string): Observable<Session> {
    return this.http.post<Session>(`${environment.apiUrl}/auth/login`, { email, motDePasse }).pipe(tap(session => { localStorage.setItem(this.key, JSON.stringify(session)); this.current.set(session); }));
  }
  hasRole(roles: Role[]): boolean { const role = this.current()?.role; return !!role && roles.includes(role); }
  logout(): void { localStorage.removeItem(this.key); this.current.set(null); void this.router.navigate(['/connexion']); }
  private readSession(): Session | null { try { return JSON.parse(localStorage.getItem(this.key) ?? 'null') as Session | null; } catch { return null; } }
}
