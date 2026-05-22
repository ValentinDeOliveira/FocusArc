import {Component, computed, DestroyRef, inject, input, OnInit, output, signal} from '@angular/core';
import {Task} from '../../../models/task.model';
import {formatSeconds} from '../../../utils/time.utils';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';

const STORAGE_KEY = 'active_task_timer';

interface PersistedTimer {
    taskId: string;
    startedAt: number; // Unix ms
}

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

    done = output<number>(); // emits overtime in minutes (0 if finished before estimate)

    private interval: ReturnType<typeof setInterval> | null = null;
    private static MAX_OVERTIME = 3600;


    complete() {
        this.clearIntervalIfExists();
        localStorage.removeItem(STORAGE_KEY);
        const overtime = Math.max(0, -this.remainingSeconds() / 60);
        this.done.emit(overtime);
    }

    ngOnInit() {
        const total = this.task().estimatedMinutes * 60;
        this.totalSeconds.set(total);

        const startedAt = this.resolveStartedAt();
        const elapsed = Math.floor((Date.now() - startedAt) / 1000);
        this.remainingSeconds.set(Math.max(total - elapsed, -DashboardTaskTimer.MAX_OVERTIME));

        this.interval = setInterval(() => {
            if (this.remainingSeconds() <= -DashboardTaskTimer.MAX_OVERTIME) {
                this.clearIntervalIfExists();
                return;
            }
            this.remainingSeconds.update(s => s - 1);
        }, 1000);

        this.destroyRef.onDestroy(() => this.clearIntervalIfExists());
    }

    // fallback in case the user refresh / close the window
    private resolveStartedAt(): number {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (raw) {
            try {
                const persisted: PersistedTimer = JSON.parse(raw);
                if (persisted.taskId === this.task().id) {
                    return persisted.startedAt;
                }
            } catch {
                // stale or corrupt entry — fall through to fresh start
            }
        }
        const now = Date.now();
        localStorage.setItem(STORAGE_KEY, JSON.stringify({ taskId: this.task().id, startedAt: now } satisfies PersistedTimer));
        return now;
    }

    private clearIntervalIfExists() {
        if (this.interval) {
            clearInterval(this.interval);
            this.interval = null;
        }
    }
}
