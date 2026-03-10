import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Task, TaskCompletedDto, TaskCreationDto, TaskUpdateDto} from '../../models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/tasks";

    getById(id: string): Observable<Task> {
        return this.http.get<Task>(`${this.baseUrl}/${id}`);
    }

    getAllForChapter(chapterId: string): Observable<Task[]> {
        return this.http.get<Task[]>(`${this.baseUrl}/chapters/${chapterId}`);
    }

    create(dto: TaskCreationDto): Observable<Task> {
        return this.http.post<Task>(`${this.baseUrl}`, dto);
    }

    update(id: string, dto: TaskUpdateDto): Observable<Task> {
        return this.http.put<Task>(`${this.baseUrl}/${id}`, dto);
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    deleteAll(arcId: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/chapters/${arcId}`);
    }

    completeTask(dto: TaskCompletedDto): Observable<void> {
        return this.http.patch<void>(`${this.baseUrl}/complete`, dto);
    }

    getTodayTask(): Observable<Task[]> {
        return this.http.get<Task[]>(`${this.baseUrl}/today`);
    }
}
