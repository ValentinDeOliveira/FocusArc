import {Component, inject} from '@angular/core';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {MatIcon} from '@angular/material/icon';
import {Tag} from '../../../models/tag.model';
import {TagPicker} from '../../../shared/tag-picker/tag-picker';
import {TaskRow} from '../../../shared/task-row/task-row';
import {TaskInfoEdit} from '../../../shared/task-info-edit/task-info-edit';
import {TaskCreationDto} from '../../../models/task.model';
import {TaskService} from '../../../core/services/task.service';
import {ContextStore} from '../../../core/stores/context.store';

@Component({
    selector: 'app-task-creation',
    imports: [
        PrimaryButton,
        MatIcon,
        TagPicker,
        TaskRow,
        TaskInfoEdit,
    ],
    templateUrl: './task-creation.html',
    styleUrl: './task-creation.css',
})
export class TaskCreation {
    private contextStore = inject(ContextStore);

    protected isTaskCreation = false;
    protected name = '';
    protected scheduledAt = '';
    protected estimatedMinutes: number | null = null;
    protected selectedTag: Tag | null = null;

    private taskService = inject(TaskService);

    protected addTask() {
        this.isTaskCreation = true;
    }

    protected cancel() {
        this.isTaskCreation = false;
        this.name = '';
        this.scheduledAt = '';
        this.estimatedMinutes = null;
        this.selectedTag = null;
    }

    protected confirm() {
        const today = new Date();
        const [hours, minutes] = this.scheduledAt.split(':').map(Number);
        today.setHours(hours, minutes, 0, 0);

        const dto: TaskCreationDto = {
            chapterId: this.contextStore.currentChapterId()!,
            estimatedMinutes: this.estimatedMinutes!,
            scheduledAt: today.toISOString(),
            tagId: !!this.selectedTag ? this.selectedTag.id : null,
            name: this.name
        }

        this.taskService.create(dto).subscribe();
    }
}
