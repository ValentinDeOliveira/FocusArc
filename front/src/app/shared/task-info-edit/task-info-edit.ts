import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from '@angular/forms';

@Component({
    selector: 'app-task-info-edit',
    imports: [FormsModule],
    templateUrl: './task-info-edit.html',
    styleUrl: './task-info-edit.css',
})
export class TaskInfoEdit {
    @Output() nameChange = new EventEmitter<string>();
    @Output() scheduledAtChange = new EventEmitter<string>();
    @Output() estimatedMinutesChange = new EventEmitter<number>();

    protected name = '';
    protected scheduledAt = '';
    protected estimatedMinutes: number | null = null;
}