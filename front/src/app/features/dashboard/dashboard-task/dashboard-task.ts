import {Component, inject, Input} from '@angular/core';
import {Task} from '../../../models/task.model';
import {MatCheckbox} from '@angular/material/checkbox';
import {DatePipe} from '@angular/common';

@Component({
    selector: 'app-dashboard-task',
    imports: [
        MatCheckbox,
    ],
    providers: [DatePipe],
    templateUrl: './dashboard-task.html',
    styleUrl: './dashboard-task.css',
})
export class DashboardTask {
    @Input() task!: Task;
    private datePipe = inject(DatePipe);

    formatTime(date: string) {
        return this.datePipe.transform(date, 'HH:mm');
    }
}
