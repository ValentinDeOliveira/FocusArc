import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Tag, TagCreationDto, TagUpdateDto} from '../../models/tag.model';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class TagService {
    private http = inject(HttpClient);
    private baseUrl = environment.apiBaseUrl + '/api/tags';

    getById(id: string): Observable<Tag> {
        return this.http.get<Tag>(`${this.baseUrl}/${id}`);
    }

    getAllForUser(): Observable<Tag[]> {
        return this.http.get<Tag[]>(`${this.baseUrl}/me`);
    }

    create(dto: TagCreationDto): Observable<Tag> {
        return this.http.post<Tag>(`${this.baseUrl}`, dto);
    }

    update(id: string, dto: TagUpdateDto): Observable<Tag> {
        return this.http.put<Tag>(`${this.baseUrl}/${id}`, dto);
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    deleteAllForUser(): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}`);
    }
}
