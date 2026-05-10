import {Component, input, output, signal} from '@angular/core';
import {padTime} from '../../utils/time.utils';

@Component({
    selector: 'app-time-field',
    imports: [],
    templateUrl: './time-field.html',
    styleUrl: './time-field.css',
})
export class TimeField {
    label = input.required<string>();
    timeChange = output<string>();

    hours = signal(9);
    minutes = signal(0);

    pad = padTime;

    onHoursInput(event: Event): void {
        const el = event.target as HTMLInputElement;
        const clamped = Math.max(0, Math.min(23, el.valueAsNumber || 0));
        el.value = String(clamped);
        this.hours.set(clamped);
        this.emit();
    }

    onMinutesInput(event: Event): void {
        const el = event.target as HTMLInputElement;
        const clamped = Math.max(0, Math.min(59, el.valueAsNumber || 0));
        el.value = String(clamped);
        this.minutes.set(clamped);
        this.emit();
    }

    onHoursBlur(event: Event): void {
        (event.target as HTMLInputElement).value = padTime(this.hours());
    }

    onMinutesBlur(event: Event): void {
        (event.target as HTMLInputElement).value = padTime(this.minutes());
    }

    private emit(): void {
        this.timeChange.emit(`${padTime(this.hours())}:${padTime(this.minutes())}`);
    }
}
