import {Component, Input} from '@angular/core';
import {TaskStatus, TaskStatusLabel} from '../../models/task.model';
import {enumToCssClass} from '../../utils/string.utils';

@Component({
    selector: 'app-task-status-badge',
    templateUrl: './task-status-badge.html',
    styleUrl: './task-status-badge.css',
})
export class TaskStatusBadge {
    @Input({required: true}) status!: TaskStatus;

    protected readonly enumToCssClass = enumToCssClass;

    get label(): string {
        return TaskStatusLabel[this.status];
    }
}
