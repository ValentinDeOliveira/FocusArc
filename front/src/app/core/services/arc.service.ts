import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Arc, ArcCreationDto, ArcSummaryResponseDto, ArcUpdateDto} from '../../models/arc.model';

@Injectable({ providedIn: 'root' })
export class ArcService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/arcs";

    getById(id: string): Observable<Arc> {
        return this.http.get<Arc>(`${this.baseUrl}/${id}`);
    }

    getAll(): Observable<Arc[]> {
        return this.http.get<Arc[]>(`${this.baseUrl}/me`);
    }

    create(dto: ArcCreationDto): Observable<Arc> {
        return this.http.post<Arc>(`${this.baseUrl}`, dto);
    }

    update(id: string, dto: ArcUpdateDto): Observable<Arc> {
        return this.http.put<Arc>(`${this.baseUrl}/${id}`, dto);
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    deleteAll(): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}`);
    }

    getSummary(): Observable<ArcSummaryResponseDto> {
        return this.http.get<ArcSummaryResponseDto>(`${this.baseUrl}/summary`);
    }
}
