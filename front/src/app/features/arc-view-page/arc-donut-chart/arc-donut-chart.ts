import {Component, Input} from '@angular/core';
import {MatCard} from '@angular/material/card';
import {ChapterState} from '../arc-view-chapter/arc-view-chapter';
import {TaskStatus} from '../../../models/task.model';

interface DonutSegment {
    label: string;
    count: number;
    color: string;
    dashArray: string;
    dashOffset: number;
}

export const CHAPTER_STATES: { state: ChapterState; label: string; color: string }[] = [
    { state: ChapterState.DONE,        label: 'Done',        color: 'var(--color-success)'          },
    { state: ChapterState.INCOMPLETE,  label: 'Incomplete',  color: 'var(--color-warning)'          },
    { state: ChapterState.IN_PROGRESS, label: 'In Progress', color: 'var(--color-in-progress-text)' },
    { state: ChapterState.PLANNED,     label: 'Planned',     color: 'var(--color-disabled)'         },
];

export const TASK_STATES: { state: TaskStatus; label: string; color: string }[] = [
    { state: TaskStatus.DONE,        label: 'Done',        color: 'var(--color-success)'          },
    { state: TaskStatus.SKIPPED,  label: 'Skipped',  color: 'var(--color-warning)'          },
    { state: TaskStatus.IN_PROGRESS, label: 'In Progress', color: 'var(--color-in-progress-text)' },
    { state: TaskStatus.PLANNED,     label: 'Planned',     color: 'var(--color-disabled)'         },
];

@Component({
    selector: 'app-arc-donut-chart',
    imports: [MatCard],
    templateUrl: './arc-donut-chart.html',
    styleUrl: './arc-donut-chart.css',
})
export class ArcDonutChart {
    @Input({ required: true }) counts!: Record<ChapterState, number>;

    readonly r = 38;
    readonly strokeWidth = 14;
    readonly circumference = 2 * Math.PI * this.r;

    get total(): number {
        return Object.values(this.counts).reduce((a, b) => a + b, 0);
    }

    get done(): number {
        return this.counts[ChapterState.DONE];
    }

    private get raw() {
        return CHAPTER_STATES
            .map(s => ({ ...s, count: this.counts[s.state] }))
            .filter(s => s.count > 0);
    }

    get segments(): DonutSegment[] {
        let cumulative = 0;
        return this.raw.map(s => {
            const arc = this.total > 0 ? (s.count / this.total) * this.circumference : 0;
            const offset = -(cumulative / (this.total || 1)) * this.circumference;
            cumulative += s.count;
            return { ...s, dashArray: `${arc} ${this.circumference}`, dashOffset: offset };
        });
    }

    get legend() {
        return this.raw;
    }
}
