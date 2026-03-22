import { Component } from '@angular/core';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {MatIcon} from '@angular/material/icon';
import {TagDot} from '../../../shared/tag-dot/tag-dot';
import {TaskStatusBadge} from '../../../shared/task-status-badge/task-status-badge';
import {TaskRow} from '../../../shared/task-row/task-row';
import {TaskInfoEdit} from '../../../shared/task-info-edit/task-info-edit';

@Component({
    selector: 'app-task-creation',
    imports: [
        PrimaryButton,
        MatIcon,
        TagDot,
        TaskStatusBadge,
        TaskRow,
        TaskInfoEdit,
    ],
    templateUrl: './task-creation.html',
    styleUrl: './task-creation.css',
})
export class TaskCreation {
    protected isTaskCreation = false;
    protected name = '';
    protected scheduledAt = '';
    protected estimatedMinutes: number | null = null;

    protected addTask() {
        this.isTaskCreation = true;
    }
}
