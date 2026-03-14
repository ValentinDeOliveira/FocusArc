import {Component, inject} from '@angular/core';
import {TaskService} from '../../../core/services/task.service';
import {Task} from '../../../models/task.model';
import {DashboardTask} from '../dashboard-task/dashboard-task';
import {toSignal} from '@angular/core/rxjs-interop';
import {ChapterService} from '../../../core/services/chapter.service';
import {DatePipe} from '@angular/common';

@Component({
    selector: 'app-dashboard-resume',
    imports: [
        DashboardTask
    ],
    providers: [DatePipe],
    templateUrl: './dashboard-resume.html',
    styleUrl: './dashboard-resume.css',
})
export class DashboardResume {
    private taskService = inject(TaskService);
    private chapterService = inject(ChapterService);
    private datePipe = inject(DatePipe);

    tasks = toSignal(this.taskService.getTodayTask(), { initialValue: [] as Task[] });
    chapterSummary = toSignal(this.chapterService.getSummary());

    todayDate() {
        return this.datePipe.transform(Date.now(), "MMMM d");
    }

    getPlannedTime() {
        const hours = Math.floor(this.chapterSummary()!.estimatedMinutes / 60);
        const minutes = this.chapterSummary()!.estimatedMinutes % 60;
        if (minutes > 0) {
            return `${hours}h ${minutes}m`;
        }
        return `${hours}h`;
    }
}
