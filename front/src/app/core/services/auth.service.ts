import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthResponseDto, GoogleAuthRequestDto, LoginRequestDto, RefreshRequestDto, RegisterRequestDto} from '../../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/auth";

    register(dto: RegisterRequestDto): Observable<AuthResponseDto> {
        return this.http.post<AuthResponseDto>(`${this.baseUrl}/register`, dto);
    }

    login(dto: LoginRequestDto): Observable<AuthResponseDto> {
        return this.http.post<AuthResponseDto>(`${this.baseUrl}/login`, dto);
    }

    refresh(dto: RefreshRequestDto): Observable<AuthResponseDto> {
        return this.http.post<AuthResponseDto>(`${this.baseUrl}/refresh`, dto);
    }

    loginWithGoogle(dto: GoogleAuthRequestDto): Observable<AuthResponseDto> {
        return this.http.post<AuthResponseDto>(`${this.baseUrl}/google`, dto);
    }
}
