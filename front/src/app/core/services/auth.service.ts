import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GoogleAuthRequestDto, LoginRequestDto, RegisterRequestDto} from '../../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/auth";

    register(dto: RegisterRequestDto): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/register`, dto);
    }

    login(dto: LoginRequestDto): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/login`, dto);
    }

    refresh(): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/refresh`, null);
    }

    loginWithGoogle(dto: GoogleAuthRequestDto): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/google`, dto);
    }

    logout(): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/logout`, null);
    }
}