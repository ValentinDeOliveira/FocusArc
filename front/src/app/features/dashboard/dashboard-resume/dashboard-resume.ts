import {Component, inject, OnInit, signal} from '@angular/core';
import {TaskService} from '../../../core/services/task.service';
import {Task, TaskCompletedDto} from '../../../models/task.model';
import {DashboardTask} from '../dashboard-task/dashboard-task';
import {ChapterService} from '../../../core/services/chapter.service';
import {DatePipe} from '@angular/common';
import {formatMinutes} from '../../../utils/time.utils';
import {DashboardTaskTimer} from '../dashboard-task-timer/dashboard-task-timer';
import {TagStore} from '../../../core/stores/tag.store';
import {switchMap} from 'rxjs';
import {TaskCreation} from '../task-creation/task-creation';
import {ContextStore} from '../../../core/stores/context.store';
import {ChapterSummaryResponseDto} from '../../../models/chapter.model';

@Component({
    selector: 'app-dashboard-resume',
    imports: [
        DashboardTask,
        DashboardTaskTimer,
        TaskCreation
    ],
    providers: [DatePipe],
    templateUrl: './dashboard-resume.html',
    styleUrl: './dashboard-resume.css',
})
export class DashboardResume implements OnInit {
    private taskService = inject(TaskService);
    private chapterService = inject(ChapterService);
    private datePipe = inject(DatePipe);
    protected tagStore = inject(TagStore);
    private contextStore = inject(ContextStore);


    tasks = signal<Task[]>([]);
    activeTask = signal<Task | null>(null);
    chapterSummary = signal<ChapterSummaryResponseDto | null>(null);

    ngOnInit(): void {
        this.tagStore.load().pipe(
            switchMap(() => this.taskService.getTodayTask())
        ).subscribe(t => this.tasks.set(t));

        this.chapterService.getSummary().subscribe(summary => {
            this.chapterSummary.set(summary);
            this.contextStore.setChapterId(summary.chapterId);
        });
    }

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

    protected onTaskCreated(task: Task) {
        this.taskService.getTodayTask().subscribe(tasks => {
            this.tasks.set(tasks);
        })
    }
}
