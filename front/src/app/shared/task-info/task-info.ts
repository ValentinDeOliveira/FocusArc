import {Component, inject, Input} from '@angular/core';
import {Tag} from '../../models/tag.model';
import {formatMinutes} from '../../utils/time.utils';
import {Task, TaskUpdateDto} from '../../models/task.model';
import {DatePipe} from '@angular/common';
import {TaskService} from '../../core/services/task.service';
import {TaskRow} from '../task-row/task-row';
import {TagPicker} from '../tag-picker/tag-picker';
import {TaskStatusBadge} from '../task-status-badge/task-status-badge';
import {isTaskEnded} from '../../utils/task.utils';

@Component({
    selector: 'app-task-info',
    imports: [
        TaskStatusBadge,
        TaskRow,
        TagPicker,
    ],
    providers: [DatePipe],
    templateUrl: './task-info.html',
    styleUrl: './task-info.css',
})
export class TaskInfo {
    @Input() task!: Task;
    @Input() tag: Tag | null = null;

    private datePipe = inject(DatePipe);
    private taskService = inject(TaskService);

    formatTime(date: string) {
        return this.datePipe.transform(date, 'HH:mm');
    }

    getEstimatedMinutes() {
        return formatMinutes(this.task.estimatedMinutes);
    }

    updateTag(updatedTag: Tag | null) {
        this.tag = updatedTag;

        const dto: TaskUpdateDto = {
            tagId: updatedTag?.id
        };

        this.taskService.update(this.task.id, dto).subscribe();
    }

    get isTaskEnded() {
        return isTaskEnded(this.task);
    }
}
