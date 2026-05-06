import {Component, inject, Input} from '@angular/core';
import {Tag} from '../../models/tag.model';
import {formatMinutes} from '../../utils/time.utils';
import {Task, TaskUpdateDto} from '../../models/task.model';
import {formatTimeHHmm} from '../utils/date-utils';
import {TaskService} from '../../core/services/task.service';
import {ArcService} from '../../core/services/arc.service';
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
    templateUrl: './task-info.html',
    styleUrl: './task-info.css',
})
export class TaskInfo {
    @Input() task!: Task;
    @Input() tag: Tag | null = null;

    private taskService = inject(TaskService);
    private arcService = inject(ArcService);

    protected readonly formatTimeHHmm = formatTimeHHmm;
    protected readonly formatMinutes = formatMinutes;
    protected readonly isTaskEnded = isTaskEnded;

    updateTag(updatedTag: Tag | null) {
        this.tag = updatedTag;

        const dto: TaskUpdateDto = {
            tagId: updatedTag?.id
        };

        this.taskService.update(this.task.id, dto).subscribe(() => {
            this.arcService.notifyStatsChanged();
        });
    }

}
