import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ArcCreationDto, ArcUpdateDto} from '../../models/arc.model';
import {Chapter, ChapterCreationDto, ChapterSummaryResponseDto, ChapterUpdateDto} from '../../models/chapter.model';

@Injectable({ providedIn: 'root' })
export class ChapterService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/chapters";

    getById(id: string): Observable<Chapter> {
        return this.http.get<Chapter>(`${this.baseUrl}/${id}`);
    }

    getAllForArc(arcId: string): Observable<Chapter[]> {
        return this.http.get<Chapter[]>(`${this.baseUrl}/arcs/${arcId}`);
    }

    create(dto: ChapterCreationDto): Observable<Chapter> {
        return this.http.post<Chapter>(`${this.baseUrl}`, dto);
    }

    update(id: string, dto: ChapterUpdateDto): Observable<Chapter> {
        return this.http.put<Chapter>(`${this.baseUrl}/${id}`, dto);
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    deleteAll(arcId: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/arcs/${arcId}`);
    }

    getSummary(): Observable<ChapterSummaryResponseDto> {
        return this.http.get<ChapterSummaryResponseDto>(`${this.baseUrl}/summary`);
    }
}
