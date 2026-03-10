import {Component, Input} from '@angular/core';
import {Task} from '../../../models/task.model';
import {MatCheckbox} from '@angular/material/checkbox';

@Component({
    selector: 'app-dashboard-task',
    imports: [
        MatCheckbox
    ],
    templateUrl: './dashboard-task.html',
    styleUrl: './dashboard-task.css',
})
export class DashboardTask {
    @Input() task!: Task;

    get startTime(): string {
        return this.formatTime(new Date(this.task.scheduledAt));
    }

    get endTime(): string {
        const end = new Date(this.task.scheduledAt);
        console.log(end.getTime());
        end.setMinutes(end.getMinutes() + this.task.estimatedMinutes);
        return this.formatTime(end);
    }

    private formatTime(date: Date): string {
        return date.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit', hour12: true });
    }
}
