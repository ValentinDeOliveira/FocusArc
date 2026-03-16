import {Component, Input} from '@angular/core';
import {TaskStatus} from '../../models/task.model';

@Component({
    selector: 'app-task-status-badge',
    templateUrl: './task-status-badge.html',
    styleUrl: './task-status-badge.css',
})
export class TaskStatusBadge {
    @Input({required: true}) status!: TaskStatus;

    get label(): string {
        const labels: Record<TaskStatus, string> = {
            PLANNED: 'Planned',
            IN_PROGRESS: 'In Progress',
            DONE: 'Done',
            SKIPPED: 'Skipped',
        };
        return labels[this.status];
    }

    get statusClass(): string {
        return this.status.toLowerCase().replace('_', '-');
    }
}