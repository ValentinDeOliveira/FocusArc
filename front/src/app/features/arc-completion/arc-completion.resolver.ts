import {inject} from '@angular/core';
import {RedirectCommand, ResolveFn, Router} from '@angular/router';
import {lastValueFrom} from 'rxjs';
import {tap} from 'rxjs/operators';
import {Arc, ArcSummaryResponseDto} from '../../models/arc.model';
import {TagStatDto} from '../../models/tag.model';
import {ArcService} from '../../core/services/arc.service';
import {ContextStore} from '../../core/stores/context.store';
import {TagStore} from '../../core/stores/tag.store';

export interface ArcCompletionData {
    arc: Arc;
    summary: ArcSummaryResponseDto;
    tagStats: TagStatDto[];
}

export const arcCompletionResolver: ResolveFn<ArcCompletionData> = async () => {
    const arcService = inject(ArcService);
    const contextStore = inject(ContextStore);
    const tagStore= inject(TagStore);

    const stored = contextStore.arcSummary();

    const arc = await lastValueFrom(arcService.getLatest()).catch(() => null);
    // if no latest, it means no completed nor active arc
    if (!arc) return new RedirectCommand(inject(Router).parseUrl('/arc-creation'));

    const [summary, tagStats] = await Promise.all([
        stored
            ? stored
            : lastValueFrom(arcService.getSummary().pipe(
                tap(s => {
                    contextStore.setSummary(s);
                    contextStore.setArcId(s.arcId);
                })
              )),
        lastValueFrom(arcService.getTagStats()).catch(() => []),
        lastValueFrom(tagStore.load()),
    ]);

    return { arc, summary, tagStats };
};
