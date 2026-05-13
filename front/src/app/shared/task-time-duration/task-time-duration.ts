import {booleanAttribute, Component, input, model, signal} from '@angular/core';
import {NumberField} from '../number-field/number-field';
import {TimeField} from '../time-field/time-field';

@Component({
    selector: 'app-task-time-duration',
    imports: [NumberField, TimeField],
    templateUrl: './task-time-duration.html',
    styleUrl: './task-time-duration.css',
})
export class TaskTimeDuration {
    startTime = model<string>('09:00');
    duration = model<number>(30);
    displayPresets = input(false, { transform: booleanAttribute });
    displayCustom = signal(false);

    readonly durationPresets = [15, 30, 45, 60, 90];

    protected onClickCustom() {
        this.displayCustom.set(true);
    }

    protected onClickPreset(preset: number) {
        this.displayCustom.set(false);
        this.duration.set(preset);
    }
}
