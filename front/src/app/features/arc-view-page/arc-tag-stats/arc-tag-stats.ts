import {Component, inject, Input} from '@angular/core';
import {TagStatDto} from '../../../models/tag.model';
import {TagStore} from '../../../core/stores/tag.store';
import {TAG_COLORS} from '../../../shared/tag-dot/tag-dot';
import {ArcStatsBase, StatRow} from '../../../shared/arc-stats-base/arc-stats-base';

@Component({
    selector: 'app-arc-tag-stats',
    imports: [ArcStatsBase],
    templateUrl: './arc-tag-stats.html',
    styleUrl: './arc-tag-stats.css',
})
export class ArcTagStats {
    private tagStore = inject(TagStore);

    @Input({ required: true }) tagStats!: TagStatDto[];

    private get allRows(): StatRow[] {
        return this.tagStats
            .filter(s => s.done > 0)
            .map(s => {
                const tag = this.tagStore.byId(s.tagId);
                return {
                    label: tag?.label ?? '?',
                    color: tag ? TAG_COLORS[tag.color] : '#6b7280',
                    done: s.done,
                    total: s.total,
                    pct: Math.round((s.done / s.total) * 100),
                };
            })
            .sort((a, b) => b.done - a.done);
    }

    get rows(): StatRow[] {
        return this.allRows.filter(r => r.pct < 100).slice(0, 4);
    }

    get completedRows(): StatRow[] {
        return this.allRows.filter(r => r.pct === 100);
    }

    get completedLabel(): string {
        const labels = this.completedRows.map(r => r.label);
        if (labels.length <= 3) return labels.join(', ');
        return `${labels.slice(0, 3).join(', ')} +${labels.length - 3} more`;
    }
}