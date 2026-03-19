import { Component, computed, DestroyRef, inject, input, OnInit, output, signal } from '@angular/core';
import { Task } from '../../../models/task.model';
import {formatSeconds} from '../../../utils/time.utils';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';

@Component({
    selector: 'app-dashboard-task-timer',
    imports: [
        PrimaryButton
    ],
    templateUrl: './dashboard-task-timer.html',
    styleUrl: './dashboard-task-timer.css',
})
export class DashboardTaskTimer implements OnInit {
    task = input.required<Task>();

    private destroyRef = inject(DestroyRef);

    readonly radius = 45;
    readonly circumference = 2 * Math.PI * this.radius;

    remainingSeconds = signal(0); // goes negative when overtime
    totalSeconds = signal(0);

    isOvertime = computed(() => this.remainingSeconds() < 0);

    progress = computed(() => {
        const total = this.totalSeconds();
        return total === 0 ? 1 : Math.max(0, this.remainingSeconds() / total);
    });

    strokeDashoffset = computed(() =>
        this.isOvertime() ? 0 : this.circumference * (1 - this.progress())
    );

    strokeColor = computed(() =>
        this.isOvertime() ? '#ff9800' : '#4fc3f7'
    );

    timeDisplay = computed(() => formatSeconds(Math.abs(this.remainingSeconds())));

    label = computed(() => this.isOvertime() ? 'OVER ESTIMATE' : 'FOCUSING');

    overtimeDisplay = computed(() =>
        this.isOvertime() ? `+${formatSeconds(-this.remainingSeconds())}` : null
    );

    doneButtonText = computed(() =>
        this.isOvertime() ? 'Done - log actual time' : 'Done'
    );

    done = output<number>(); // emits overtime in seconds (0 if finished before estimate)

    private interval: ReturnType<typeof setInterval> | null = null;

    complete() {
        this.clearIntervalIfExists();
        const overtime = Math.max(0, -this.remainingSeconds());
        this.done.emit(overtime);
    }

    ngOnInit() {
        const total = this.task().estimatedMinutes * 60;
        this.totalSeconds.set(total);
        this.remainingSeconds.set(total);

        this.interval = setInterval(() => {
            this.remainingSeconds.update(s => s - 1);
        }, 0.5);

        this.destroyRef.onDestroy(() => this.clearIntervalIfExists());
    }

    private clearIntervalIfExists() {
        if (this.interval) {
            clearInterval(this.interval);
            this.interval = null;
        }
    }
}
