import {Component, computed, signal} from '@angular/core';
import {NumberField} from '../number-field/number-field';
import {RecurrenceLabel, RecurrenceType} from '../../models/recurrence.model';
import {DAY_ABBREV_TO_ENUM, DayOfWeek} from '../../utils/date.utils';
import {TaskTimeDuration} from '../task-time-duration/task-time-duration';

export interface RecurrenceConfig {
    recurrence: RecurrenceType;
    daysOfWeek: DayOfWeek[];
    everyNDays: number;
    startTime: string;
    duration: number;
}

@Component({
    selector: 'app-arc-task-recurrence',
    imports: [
        NumberField,
        TaskTimeDuration,
    ],
    templateUrl: './arc-task-recurrence.html',
    styleUrl: './arc-task-recurrence.css',
})
export class ArcTaskRecurrence {
    readonly options = Object.values(RecurrenceType);
    selected = signal<RecurrenceType>(RecurrenceType.DAILY);
    protected readonly RecurrenceType = RecurrenceType;
    protected readonly RecurrenceLabel = RecurrenceLabel;

    readonly days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    selectedDays = signal<Set<string>>(new Set(['Mon', 'Tue', 'Wed', 'Thu', 'Fri']));

    duration = signal(30);
    startTime = signal('09:00');
    everyNDays = signal(2);

    config = computed<RecurrenceConfig>(() => ({
        recurrence: this.selected(),
        daysOfWeek: [...this.selectedDays()].map(d => DAY_ABBREV_TO_ENUM[d]),
        everyNDays: this.everyNDays(),
        startTime: this.startTime(),
        duration: this.duration(),
    }));

    toggleDay(day: string): void {
        this.selectedDays.update(set => {
            if (set.has(day) && set.size === 1) return set;
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
