import {Chapter} from '../../models/chapter.model';
import {ResolveFn} from '@angular/router';
import {ChapterService} from '../../core/services/chapter.service';
import {inject} from '@angular/core';
import {ArcService} from '../../core/services/arc.service';
import {ContextStore} from '../../core/stores/context.store';
import {map, tap} from 'rxjs/operators';
import {forkJoin, switchMap} from 'rxjs';
import {TagStore} from '../../core/stores/tag.store';

export const arcResolver: ResolveFn<Chapter[]> = () => {
    const contextStore = inject(ContextStore);
    const chapterService = inject(ChapterService);
    const tagStore = inject(TagStore);

    const arcId = contextStore.currentArcId();

    const load = (arcId: string) => forkJoin({
        chapters: chapterService.getAllForArc(arcId),
        tags: tagStore.load()
    }).pipe(map(r => r.chapters));

    if(arcId) {
       return load(arcId);
    }

    const arcService = inject(ArcService);
    return arcService.getSummary().pipe(
        tap(summary => {
            contextStore.setSummary(summary);
            contextStore.setArcId(summary.arcId);
        }),
        switchMap(summary => load(summary.arcId))
    );
}
