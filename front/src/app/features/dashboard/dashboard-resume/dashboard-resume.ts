import {Component, inject, OnInit, signal} from '@angular/core';
import {TaskService} from '../../../core/services/task.service';
import {Task, TaskCompletedDto} from '../../../models/task.model';
import {DashboardTask} from '../dashboard-task/dashboard-task';
import {ChapterService} from '../../../core/services/chapter.service';
import {formatDateLong} from '../../../utils/date.utils';
import {formatMinutes} from '../../../utils/time.utils';
import {DashboardTaskTimer} from '../dashboard-task-timer/dashboard-task-timer';
import {TagStore} from '../../../core/stores/tag.store';
import {forkJoin, of, switchMap} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {TaskCreation} from '../task-creation/task-creation';
import {ContextStore} from '../../../core/stores/context.store';
import {ChapterSummaryResponseDto} from '../../../models/chapter.model';
import {ArcService} from '../../../core/services/arc.service';
import {HttpErrorResponse} from '@angular/common/http';
import {ApiErrorType} from '../../../models/api-error.model';

@Component({
    selector: 'app-dashboard-resume',
    imports: [
        DashboardTask,
        DashboardTaskTimer,
        TaskCreation
    ],
    templateUrl: './dashboard-resume.html',
    styleUrl: './dashboard-resume.css',
})
export class DashboardResume implements OnInit {
    private taskService = inject(TaskService);
    private chapterService = inject(ChapterService);
    protected tagStore = inject(TagStore);
    private contextStore = inject(ContextStore);
    private arcService = inject(ArcService);

    tasks = signal<Task[]>([]);
    activeTask = signal<Task | null>(null);
    chapterSummary = signal<ChapterSummaryResponseDto | null>(null);

    ngOnInit(): void {
        forkJoin({
            tags: this.tagStore.load(),
            summary: this.chapterService.getSummary()
        }).pipe(
            tap(({ summary }) => {
                this.chapterSummary.set(summary);
                this.contextStore.setChapterId(summary.chapterId);
            }),
            switchMap(() => this.taskService.getTodayTask()),
            catchError((err: HttpErrorResponse) => {
                /*
                * Right now the only possible error is if no task schedule for that day
                * It means there's no chapter, hence summary fail
                * TODO: fix backend to not crash in case of no chapter
                * TODO 2: OR as soon as a user create a task on a chapter without task, create the chapter first
                *         (+ link it with other data like update summary progress bar) and create the task
                * */
                if (err.error.error == ApiErrorType.NoChapterForArcException) {
                    return of([]);
                }
                throw err;
            })
        ).subscribe(tasks => this.tasks.set(tasks));
    }

    todayDate() {
        return formatDateLong(Date.now());
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

        this.taskService.completeTask(this.activeTask()!.id, dto).pipe(
            switchMap(updatedTask => {
                this.tasks.update(l => l.map(t => t.id === updatedTask.id ? updatedTask : t));
                this.activeTask.set(null);
                return this.arcService.getSummary();
            })
        ).subscribe(summary => {
            this.contextStore.setSummary(summary);
        });
    }

    protected setActiveTask(task: Task) {
        this.taskService.startTask(task.id).subscribe(updatedTask => {
            this.tasks.update(l => l.map(task => task.id == updatedTask.id ? updatedTask : task));
            this.activeTask.set(task);
        });
    }

    protected onTaskCreated() {
        this.taskService.getTodayTask().subscribe(tasks => {
            this.tasks.set(tasks);
        })
    }
}
