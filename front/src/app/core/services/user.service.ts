import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User, UserUpdateDto} from '../../models/user.model';

@Injectable({ providedIn: 'root' })
export class ChapterService {
    private http = inject(HttpClient);
    private baseUrl = "http://localhost:8080/api/users";

    getById(id: string): Observable<User> {
        return this.http.get<User>(`${this.baseUrl}/${id}`);
    }

    update(dto: UserUpdateDto): Observable<User> {
        return this.http.put<User>(`${this.baseUrl}`, dto);
    }

    delete(): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}`);
    }
}
