import {Component, Input, output} from '@angular/core';
import {Task} from '../../../models/task.model';
import {DatePipe} from '@angular/common';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {TaskInfo} from '../../../shared/task-info/task-info';
import {isTaskEnded} from '../../../utils/task.utils';
import {Tag} from '../../../models/tag.model';

@Component({
    selector: 'app-dashboard-task',
    imports: [
        PrimaryButton,
        TaskInfo,

    ],
    providers: [DatePipe],
    templateUrl: './dashboard-task.html',
    styleUrl: './dashboard-task.css',
})
export class DashboardTask {
    @Input() task!: Task;
    @Input() tag: Tag | null = null;

    @Input() hasActiveTask = false;
    started = output<Task>();

    startTask() {
        this.started.emit(this.task);
    }

    get isTaskEnded() {
        return isTaskEnded(this.task);
    }
}
