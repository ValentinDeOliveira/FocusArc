import {Component, inject, Input, output} from '@angular/core';
import {Task, TaskUpdateDto} from '../../../models/task.model';
import {Tag} from '../../../models/tag.model';
import {DatePipe} from '@angular/common';
import {formatMinutes} from '../../../utils/time.utils';
import {TaskStatusBadge} from '../../../shared/task-status-badge/task-status-badge';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {TaskRow} from '../../../shared/task-row/task-row';
import {TagPicker} from '../../../shared/tag-picker/tag-picker';
import {TaskService} from '../../../core/services/task.service';

@Component({
    selector: 'app-dashboard-task',
    imports: [
        TaskStatusBadge,
        PrimaryButton,
        TaskRow,
        TagPicker,
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

    private datePipe = inject(DatePipe);
    private taskService = inject(TaskService);

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

    updateTag(updatedTag: Tag | null) {
        this.tag = updatedTag;

        const dto: TaskUpdateDto = {
            tagId: updatedTag?.id
        };

        this.taskService.update(this.task.id, dto).subscribe();
    }
}
