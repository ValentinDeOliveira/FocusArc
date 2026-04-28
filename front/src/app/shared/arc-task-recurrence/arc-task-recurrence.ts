import {Component, computed, signal} from '@angular/core';
import {NumberField} from '../number-field/number-field';
import {TimeField} from '../time-field/time-field';

export enum RecurrenceType {
    DAILY = 'Daily',
    DAYS_OF_WEEK = 'Days of week',
    EVERY_N_DAYS = 'Every N Days',
}

export interface RecurrenceConfig {
    recurrence: RecurrenceType;
    daysOfWeek: string[];
    everyNDays: number;
    startTime: string;
    duration: number;
}

@Component({
    selector: 'app-arc-task-recurrence',
    imports: [
        NumberField,
        TimeField,
    ],
    templateUrl: './arc-task-recurrence.html',
    styleUrls: [
        './arc-task-recurrence.css',
        '../../shared/form-shared.css'
    ],
})
export class ArcTaskRecurrence {
    readonly options = Object.values(RecurrenceType);
    selected = signal<RecurrenceType>(RecurrenceType.DAILY);
    protected readonly RecurrenceType = RecurrenceType;

    readonly days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    selectedDays = signal<Set<string>>(new Set(['Mon', 'Tue', 'Wed', 'Thu', 'Fri']));

    duration = signal(30);
    readonly durationPresets = [15, 30, 60];
    startTime = signal('09:00');
    everyNDays = signal(2);

    config = computed<RecurrenceConfig>(() => ({
        recurrence: this.selected(),
        daysOfWeek: [...this.selectedDays()],
        everyNDays: this.everyNDays(),
        startTime: this.startTime(),
        duration: this.duration(),
    }));

    toggleDay(day: string): void {
        this.selectedDays.update(set => {
            const next = new Set(set);
            next.has(day) ? next.delete(day) : next.add(day);
            return next;
        });
    }

    reset(): void {
        this.selected.set(RecurrenceType.DAILY);
        this.selectedDays.set(new Set(['Mon', 'Tue', 'Wed', 'Thu', 'Fri']));
        this.duration.set(30);
        this.startTime.set('09:00');
        this.everyNDays.set(2);
    }
}