import {Component, signal} from '@angular/core';
import {InputField} from '../input-field/input-field';
import {NumberField} from '../number-field/number-field';

export enum RecurrenceType {
    DAILY = 'Daily',
    DAYS_OF_WEEK = 'Days of week',
    EVERY_N_DAYS = 'Every N Days',
}

@Component({
    selector: 'app-arc-task-recurrence',
    imports: [
        InputField,
        NumberField
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

    duration = signal(30);
    readonly durationPresets = [15, 30, 60];
    selectedDays = signal<Set<string>>(new Set(['Mon', 'Tue', 'Wed', 'Thu', 'Fri']));

    toggleDay(day: string) {
        this.selectedDays.update(set => {
            const next = new Set(set);
            next.has(day) ? next.delete(day) : next.add(day);
            return next;
        });
    }
}
