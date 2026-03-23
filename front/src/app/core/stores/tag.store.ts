import {computed, inject, Injectable, signal} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Tag} from '../../models/tag.model';
import {TagService} from '../services/tag.service';

@Injectable({ providedIn: 'root' })
export class TagStore {
    private tagService = inject(TagService);

    private tags = signal<Tag[]>([]);
    private tagMap = computed(() => new Map(this.tags().map(t => [t.id, t])));

    all = computed(() => this.tags());

    load(): Observable<Tag[]> {
        return this.tagService.getAllForUser().pipe(
            tap(tags => this.tags.set(tags))
        );
    }

    byId(id: string): Tag | null {
        return this.tagMap().get(id) ?? null;
    }
}
