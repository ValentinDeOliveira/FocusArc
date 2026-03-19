import {Component, inject, Input, output} from '@angular/core';
import {Task} from '../../../models/task.model';
import {MatCheckbox} from '@angular/material/checkbox';
import {DatePipe} from '@angular/common';
import {formatMinutes} from '../../../utils/time.utils';
import {TaskStatusBadge} from '../../../shared/task-status-badge/task-status-badge';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';

@Component({
    selector: 'app-dashboard-task',
    imports: [
        MatCheckbox,
        TaskStatusBadge,
        PrimaryButton,
    ],
    providers: [DatePipe],
    templateUrl: './dashboard-task.html',
    styleUrl: './dashboard-task.css',
})
export class DashboardTask {
    @Input() task!: Task;
    @Input() hasActiveTask = false;
    started = output<Task>();

    private datePipe = inject(DatePipe);

    formatTime(date: string) {
        return this.datePipe.transform(date, 'HH:mm');
    }

    getEstimatedMinutes() {
        return formatMinutes(this.task.estimatedMinutes);
    }

    get isTaskEnded() {
        return this.task.status === "DONE" || this.task.status === "SKIPPED";
    }

    startTask() {
        this.started.emit(this.task);
    }
}
