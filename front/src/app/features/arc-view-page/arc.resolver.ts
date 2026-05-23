import {inject} from '@angular/core';
import {ResolveFn} from '@angular/router';
import {lastValueFrom} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Chapter} from '../../models/chapter.model';
import {ChapterService} from '../../core/services/chapter.service';
import {ArcService} from '../../core/services/arc.service';
import {ContextStore} from '../../core/stores/context.store';
import {TagStore} from '../../core/stores/tag.store';

export const arcResolver: ResolveFn<Chapter[]> = async () => {
    const contextStore = inject(ContextStore);
    const chapterService = inject(ChapterService);
    const tagStore= inject(TagStore);

    const stored = contextStore.arcSummary();

    const arcId = stored
        ? stored.arcId
        : await lastValueFrom(inject(ArcService).getSummary().pipe(
            tap(s => { contextStore.setSummary(s); contextStore.setArcId(s.arcId); })
          )).then(s => s.arcId);

    const [chapters] = await Promise.all([
        lastValueFrom(chapterService.getAllForArc(arcId)),
        lastValueFrom(tagStore.load()),
    ]);

    return chapters;
};
