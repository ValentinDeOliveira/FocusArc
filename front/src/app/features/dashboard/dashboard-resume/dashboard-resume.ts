import {Component, inject, Input} from '@angular/core';
import {TaskService} from '../../../core/services/task.service';
import {Task} from '../../../models/task.model';
import {DashboardTask} from '../dashboard-task/dashboard-task';
import {toSignal} from '@angular/core/rxjs-interop';
import {ChapterService} from '../../../core/services/chapter.service';
import {DatePipe} from '@angular/common';
import {formatMinutes} from '../../../utils/time.utils';

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
        return formatMinutes(this.chapterSummary()!.estimatedMinutes);
    }

    get getNumberOfCompletedTasks() {
        let n = 0;
        for (let task of this.tasks()) {
            if (task.status == "DONE") n++;
        }
        return n;
    }
}
