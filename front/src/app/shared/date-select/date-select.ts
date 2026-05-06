import {booleanAttribute, Component, effect, input, OnInit, output, signal, untracked} from '@angular/core';
import {toKebabCase} from '../../utils/string.utils';
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
export class DateSelect implements OnInit {
    label = input.required<string>();
    min = input<Date | null>(null);
    defaultToday = input(false, { transform: booleanAttribute });
    error = input<string | null>(null);
    value = input<Date | null | undefined>(undefined);
    dateChange = output<Date | null>();

    selectedDate = signal<Date | null>(null);

    constructor() {
        effect(() => {
            const external = this.value();
            if (external !== undefined) {
                untracked(() => this.selectedDate.set(external));
            }
        });
    }

    ngOnInit(): void {
        if (this.defaultToday()) {
            this.selectedDate.set(new Date());
        }
    }

    onDateChange(value: Date | null): void {
        this.selectedDate.set(value);
        this.dateChange.emit(value);
    }

    get id(): string {
        return `date-${toKebabCase(this.label())}`;
    }

    // Display MM/DD/YYYY or DD/MM/YYYY depending on user
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
}
