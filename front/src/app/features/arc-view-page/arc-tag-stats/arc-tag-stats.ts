import {Component, inject, Input} from '@angular/core';
import {MatCard} from '@angular/material/card';
import {TagStatDto} from '../../../models/tag.model';
import {TagStore} from '../../../core/stores/tag.store';
import {TAG_COLORS} from '../../../shared/tag-dot/tag-dot';

interface TagStatRow {
    label: string;
    color: string;
    done: number;
    total: number;
    pct: number;
}

@Component({
    selector: 'app-arc-tag-stats',
    imports: [MatCard],
    templateUrl: './arc-tag-stats.html',
    styleUrl: './arc-tag-stats.css',
})
export class ArcTagStats {
    private tagStore = inject(TagStore);

    @Input({ required: true }) tagStats!: TagStatDto[];

    get rows(): TagStatRow[] {
        return this.tagStats
            .filter(s => s.total > 0)
            .map(s => {
                const tag = this.tagStore.byId(s.tagId);
                return {
                    label: tag?.label ?? '?',
                    color: tag ? TAG_COLORS[tag.color] : '#6b7280',
                    done: s.done,
                    total: s.total,
                    pct: Math.round((s.done / s.total) * 100),
                };
            });
    }
}