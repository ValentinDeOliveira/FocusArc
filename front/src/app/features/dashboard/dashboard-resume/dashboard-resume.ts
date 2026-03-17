import {Component, inject, signal} from '@angular/core';
import {TaskService} from '../../../core/services/task.service';
import {Task, TaskCompletedDto} from '../../../models/task.model';
import {DashboardTask} from '../dashboard-task/dashboard-task';
import {toSignal} from '@angular/core/rxjs-interop';
import {ChapterService} from '../../../core/services/chapter.service';
import {DatePipe} from '@angular/common';
import {formatMinutes} from '../../../utils/time.utils';
import {DashboardTaskTimer} from '../dashboard-task-timer/dashboard-task-timer';

@Component({
    selector: 'app-dashboard-resume',
    imports: [
        DashboardTask,
        DashboardTaskTimer
    ],
    providers: [DatePipe],
    templateUrl: './dashboard-resume.html',
    styleUrl: './dashboard-resume.css',
})
export class DashboardResume {
    private taskService = inject(TaskService);
    private chapterService = inject(ChapterService);
    private datePipe = inject(DatePipe);

    tasks = signal<Task[]>([]);

    constructor() {
        this.taskService.getTodayTask().subscribe(t => this.tasks.set(t));
    }
    activeTask = signal<Task | null>(null);
    chapterSummary = toSignal(this.chapterService.getSummary());

    todayDate() {
        return this.datePipe.transform(Date.now(), "MMMM d");
    }

    getPlannedTime() {
        return formatMinutes(this.chapterSummary()!.estimatedMinutes);
    }

    get getNumberOfCompletedTasks() {
        return this.tasks().filter((task) => task.status == "DONE").length;
    }

    protected onTaskDone(overtime: number) {
        let finalTime = this.activeTask()!.estimatedMinutes;
        if (overtime != null) {
            finalTime += overtime;
        }

        const dto: TaskCompletedDto = {
            completedMinutes: finalTime
        }

        this.taskService.completeTask(this.activeTask()!.id, dto).subscribe(updatedTask => {
            this.tasks.update(l => l.map(task => task.id == updatedTask.id ? updatedTask : task));
            this.activeTask.set(null);
        });
    }

    protected setActiveTask(task: Task) {
        this.taskService.startTask(task.id).subscribe(updatedTask => {
            this.tasks.update(l => l.map(task => task.id == updatedTask.id ? updatedTask : task));
            this.activeTask.set(task);
        });
    }
}
