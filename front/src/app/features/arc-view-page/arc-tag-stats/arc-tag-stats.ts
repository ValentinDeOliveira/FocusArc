import {Component, inject, Input} from '@angular/core';
import {TagStatDto} from '../../../models/tag.model';
import {colorOrNeutral} from '../../../models/tag-colors';
import {TagStore} from '../../../core/stores/tag.store';
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
                    color: colorOrNeutral(tag?.color),
                    done: s.done,
                    total: s.total,
                    pct: Math.round((s.done / s.total) * 100),
                };
            })
            .sort((a, b) => b.done - a.done);
    }

    private readonly MAX_ROWS = 4;

    private get incompleteRows(): StatRow[] {
        return this.allRows.filter(r => r.pct < 100);
    }

    private get allCompletedRows(): StatRow[] {
        return this.allRows.filter(r => r.pct === 100);
    }

    get rows(): StatRow[] {
        const incomplete = this.incompleteRows.slice(0, this.MAX_ROWS);
        const slots = this.MAX_ROWS - incomplete.length;
        return slots > 0
            ? [...incomplete, ...this.allCompletedRows.slice(0, slots)]
            : incomplete;
    }

    get completedRows(): StatRow[] {
        const promoted = Math.max(0, this.MAX_ROWS - this.incompleteRows.length);
        return this.allCompletedRows.slice(promoted);
    }

    get completedLabel(): string {
        const labels = this.completedRows.map(r => r.label);
        if (labels.length <= 3) return labels.join(', ');
        return `${labels.slice(0, 3).join(', ')} +${labels.length - 3} more`;
    }
}
