import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {TaskNameField} from '../input-field/task-name-field/task-name-field';

@Component({
    selector: 'app-task-info-edit',
    imports: [FormsModule, TaskNameField],
    templateUrl: './task-info-edit.html',
    styleUrl: './task-info-edit.css',
})
export class TaskInfoEdit {
    @Output() nameChange = new EventEmitter<string>();
    @Output() scheduledAtChange = new EventEmitter<string>();
    @Output() estimatedMinutesChange = new EventEmitter<number>();

    protected scheduledAt = '';
    protected estimatedMinutes: number | null = null;
}