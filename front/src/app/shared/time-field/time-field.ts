import {booleanAttribute, Component, input, OnInit, output, signal} from '@angular/core';
import {isTimeAfterNow, padTime} from '../../utils/time.utils';

@Component({
    selector: 'app-time-field',
    imports: [],
    templateUrl: './time-field.html',
    styleUrl: './time-field.css',
})
export class TimeField implements OnInit {
    label = input.required<string>();
    timeChange = output<string>();

    hours = signal(9);
    minutes = signal(0);
    verifyInvalidTime = input(false, { transform: booleanAttribute });

    protected readonly pad = padTime;

    ngOnInit(): void {
        const now = new Date();
        const totalMinutes = now.getHours() * 60 + now.getMinutes() + 1;
        this.hours.set(Math.floor(totalMinutes / 60) % 24);
        this.minutes.set(totalMinutes % 60);
    }

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

    get hasErrors() {
        return this.verifyInvalidTime() && !isTimeAfterNow(this.hours(), this.minutes())
    }

    reset() {
        this.hours.set(9);
        this.minutes.set(0);
    }

    private emit(): void {
        this.timeChange.emit(`${padTime(this.hours())}:${padTime(this.minutes())}`);
    }

}
