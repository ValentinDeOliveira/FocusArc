import {Component, EventEmitter, inject, Output, output} from '@angular/core';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {MatIcon} from '@angular/material/icon';
import {Tag} from '../../../models/tag.model';
import {TaskRow} from '../../../shared/task-row/task-row';
import {TaskCreationDto} from '../../../models/task.model';
import {TaskService} from '../../../core/services/task.service';
import {ContextStore} from '../../../core/stores/context.store';
import {HttpErrorResponse} from '@angular/common/http';
import {getTaskError} from '../../../models/errors/task-error.model';
import {ToastrService} from 'ngx-toastr';
import {TagSelector} from '../../../shared/tag-selector/tag-selector';
import {TaskNameField} from '../../../shared/input-field/task-name-field/task-name-field';
import {TaskTimeDuration} from '../../../shared/task-time-duration/task-time-duration';

@Component({
    selector: 'app-task-creation',
    imports: [
        PrimaryButton,
        MatIcon,
        TaskRow,
        TagSelector,
        TaskNameField,
        TaskTimeDuration,
    ],
    templateUrl: './task-creation.html',
    styleUrl: './task-creation.css',
})
export class TaskCreation {
    @Output() nameChange = new EventEmitter<string>();

    protected isTaskCreation = false;
    protected name = '';
    protected scheduledAt = '';
    protected estimatedMinutes: number | null = null;
    protected selectedTag: Tag | null = null;

    toastr = inject(ToastrService);


    private contextStore = inject(ContextStore);
    private taskService = inject(TaskService);
    taskCreated = output<void>();

    protected addTask() {
        this.isTaskCreation = true;
    }

    protected onTagChange(tag: Tag | null) {
        this.selectedTag = tag;
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

        this.taskService.create(dto).subscribe({
            next: () => {
                this.taskCreated.emit();
                this.isTaskCreation = false;
            },
            error: (err: HttpErrorResponse) => {
                const message = getTaskError(err.error?.error);
                this.toastr.error(message);
            }
        });
    }
}
