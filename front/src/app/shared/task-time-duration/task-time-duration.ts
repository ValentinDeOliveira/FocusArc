import {Component, model, signal} from '@angular/core';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {NumberField} from '../number-field/number-field';
import {TimeField} from '../time-field/time-field';

@Component({
    selector: 'app-task-time-duration',
    imports: [NumberField, TimeField, MatMenu, MatMenuItem, MatMenuTrigger],
    templateUrl: './task-time-duration.html',
    styleUrl: './task-time-duration.css',
})
export class TaskTimeDuration {
    startTime = model<string>('09:00');
    duration = model<number>(30);
    displayCustom = signal(false);

    readonly durationPresets = [15, 30, 45, 60, 90];

    protected selectPresetMobile(preset: number) {
        this.displayCustom.set(false);
        this.duration.set(preset);
    }

    protected onCustomInput(raw: string) {
        const value = Math.min(480, Math.max(5, Number(raw)));
        if (!isNaN(value)) {
            this.displayCustom.set(true);
            this.duration.set(value);
        }
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
}