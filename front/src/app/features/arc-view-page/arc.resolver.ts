import {Chapter} from '../../models/chapter.model';
import {ResolveFn} from '@angular/router';
import {ChapterService} from '../../core/services/chapter.service';
import {inject} from '@angular/core';
import {ArcService} from '../../core/services/arc.service';
import {ContextStore} from '../../core/stores/context.store';
import {tap} from 'rxjs/operators';
import {switchMap} from 'rxjs';

export const arcResolver: ResolveFn<Chapter[]> = () => {
    const contextStore = inject(ContextStore);
    const chapterService = inject(ChapterService);

    const arcId = contextStore.currentArcId();

    if(arcId) {
       return chapterService.getAllForArc(arcId);
    }

    const arcService = inject(ArcService);
    return arcService.getSummary().pipe(
        tap(summary => {
            contextStore.setSummary(summary);
            contextStore.setArcId(summary.arcId);
        }),
        switchMap(summary => chapterService.getAllForArc(summary.arcId))
    );
}
