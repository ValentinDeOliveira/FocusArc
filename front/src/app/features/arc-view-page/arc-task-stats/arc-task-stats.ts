import {Component, Input} from '@angular/core';
import {MatCard} from '@angular/material/card';
import {TaskStatDto} from '../../../models/task.model';
import {TASK_STATES} from '../arc-donut-chart/arc-donut-chart';

interface TaskStatRow {
    label: string;
    color: string;
    done: number;
    total: number;
    pct: number;
}

@Component({
    selector: 'app-arc-task-stats',
    imports: [MatCard],
    templateUrl: './arc-task-stats.html',
    styleUrl: './arc-task-stats.css',
})
export class ArcTaskStats {
    @Input({ required: true }) taskStats!: TaskStatDto[];

    get rows(): TaskStatRow[] {
        return this.taskStats
            .filter(s => s.total > 0)
            .map(s => {
                const taskState = TASK_STATES.filter(cs => cs.state == s.taskStatus).at(0);
                return {
                    label: taskState?.label ?? '?',
                    color: taskState?.color ?? '#6b7280',
                    done: s.done,
                    total: s.total,
                    pct: Math.round((s.done / s.total) * 100),
                };
            })
            .sort((a, b) => b.done - a.done);
    }
}
