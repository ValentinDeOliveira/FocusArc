import {booleanAttribute, Component, input, output} from '@angular/core';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';

@Component({
    selector: 'app-date-select',
    imports: [
        MatDatepicker,
        MatDatepickerInput,
        MatDatepickerToggle,
    ],
    templateUrl: './date-select.html',
    styleUrls: [
        './date-select.css',
        '../form-shared.css',
    ],
})
export class DateSelect {
    label = input.required<string>();
    min = input<Date | null>(null);
    defaultToday = input(false, { transform: booleanAttribute });
    dateChange = output<Date | null>();

    get id(): string {
        return `date-${this.label().toLowerCase().replace(/\s+/g, '-')}`;
    }

    get placeholder(): string {
        return new Intl.DateTimeFormat(navigator.language, {
            year: 'numeric', month: '2-digit', day: '2-digit',
        })
        .formatToParts(new Date(1999, 0, 21))
        .map(part => {
            switch (part.type) {
                case 'month': return 'MM';
                case 'day':   return 'DD';
                case 'year':  return 'YYYY';
                default:      return part.value;
            }
        })
        .join('');
    }

    get defaultDate(): Date | null {
        return this.defaultToday() ? new Date() : null;
    }
}
