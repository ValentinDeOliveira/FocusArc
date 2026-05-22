import {booleanAttribute, Component, input, model, signal, ViewChild} from '@angular/core';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {NumberField} from '../number-field/number-field';
import {TimeField} from '../time-field/time-field';

@Component({
    selector: 'app-task-time-duration',
    host: { '[class.compact]': 'compact()' },
    imports: [NumberField, TimeField, MatMenu, MatMenuItem, MatMenuTrigger],
    templateUrl: './task-time-duration.html',
    styleUrl: './task-time-duration.css',
})
export class TaskTimeDuration {
    @ViewChild(TimeField) private timeField!: TimeField;

    compact = input(false, { transform: booleanAttribute });
    verifyInvalidTime = input(false, { transform: booleanAttribute });
    startTime = model<string>('09:00');
    duration = model<number>(30);
    displayCustom = signal(false);

    readonly durationPresets = [15, 30, 45, 60, 90];

    protected selectPresetMobile(preset: number) {
        this.displayCustom.set(false);
        this.duration.set(preset);
    }

    protected onClickCustom() {
        this.displayCustom.set(true);
    }

    protected selectCustomMobile() {
        this.displayCustom.set(true);
    }

    protected onClickPreset(preset: number) {
        this.displayCustom.set(false);
        this.duration.set(preset);
    }

    validate(): boolean {
        return !this.timeField.hasErrors;
    }

    getStartDate(): Date {
        const date = new Date();
        date.setHours(this.timeField.hours(), this.timeField.minutes(), 0, 0);
        return date;
    }

    reset() {
        this.timeField.reset();
        this.duration.set(30);
    }
}
