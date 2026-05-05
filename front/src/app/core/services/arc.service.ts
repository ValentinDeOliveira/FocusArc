import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {Arc, ArcCreationDto, ArcSummaryResponseDto, ArcUpdateDto} from '../../models/arc.model';
import {TagStatDto} from '../../models/tag.model';
import {TaskRecurrenceDto, TaskStatDto} from '../../models/task.model';

@Injectable({ providedIn: 'root' })
export class ArcService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/arcs";

    private statsChangedSubject = new Subject<void>();
    readonly statsChanged$ = this.statsChangedSubject.asObservable();

    notifyStatsChanged(): void {
        this.statsChangedSubject.next();
    }

    getById(id: string): Observable<Arc> {
        return this.http.get<Arc>(`${this.baseUrl}/${id}`);
    }

    getAll(): Observable<Arc[]> {
        return this.http.get<Arc[]>(`${this.baseUrl}/me`);
    }

    getActive(): Observable<Arc> {
        return this.http.get<Arc>(`${this.baseUrl}/me/active`);
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

    getTagStats(): Observable<TagStatDto[]> {
        return this.http.get<TagStatDto[]>(`${this.baseUrl}/tag-stats`);
    }

    getTaskStats(): Observable<TaskStatDto[]> {
        return this.http.get<TaskStatDto[]>(`${this.baseUrl}/task-stats`);
    }

    massCreate(id: string, tasksRecurrence: TaskRecurrenceDto[]): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/${id}/tasks/init`, tasksRecurrence);
    }
}
