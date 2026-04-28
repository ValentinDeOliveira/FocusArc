import {Component, computed, input} from '@angular/core';
import {CalendarDateFormatter, CalendarEvent, CalendarWeekViewComponent, DateFormatterParams} from 'angular-calendar';
import {ArcTask} from '../arc-task.model';
import {RecurrenceType} from '../arc-task-recurrence/arc-task-recurrence';

class WeekScheduleDateFormatter extends CalendarDateFormatter {
    override weekViewColumnHeader({date, locale}: DateFormatterParams): string {
        return new Intl.DateTimeFormat(locale, {weekday: 'short'}).format(date).toUpperCase();
    }

    override weekViewColumnSubHeader({date, locale}: DateFormatterParams): string {
        return new Intl.DateTimeFormat(locale, {day: 'numeric'}).format(date);
    }
}

const DAY_INDEX: Record<string, number> = {
    Mon: 0, Tue: 1, Wed: 2, Thu: 3, Fri: 4, Sat: 5, Sun: 6,
};

const EVENT_COLOR = {primary: '#3b5bdb', secondary: '#e8ecff'};

@Component({
    selector: 'app-week-schedule',
    imports: [CalendarWeekViewComponent],
    templateUrl: './week-schedule.html',
    styleUrl: './week-schedule.css',
    providers: [{provide: CalendarDateFormatter, useClass: WeekScheduleDateFormatter}],
})
export class WeekSchedule {
    tasks = input<ArcTask[]>([]);

    readonly viewDate = new Date();

    private readonly monday = (() => {
        const d = new Date();
        const day = d.getDay();
        d.setDate(d.getDate() + (day === 0 ? -6 : 1 - day));
        d.setHours(0, 0, 0, 0);
        return d;
    })();

    calendarEvents = computed<CalendarEvent[]>(() => {
        const events: CalendarEvent[] = [];

        for (const task of this.tasks()) {
            const [h, m] = task.startTime.split(':').map(Number);
            const durationMs = task.duration * 60 * 1000;

            let activeDays: number[];
            if (task.recurrence === RecurrenceType.DAILY) {
                activeDays = [0, 1, 2, 3, 4, 5, 6];
            } else if (task.recurrence === RecurrenceType.DAYS_OF_WEEK) {
                activeDays = (task.daysOfWeek ?? []).map(d => DAY_INDEX[d]).filter(d => d !== undefined);
            } else {
                activeDays = [];
                const n = task.everyNDays ?? 2;
                for (let i = 0; i < 7; i += n) activeDays.push(i);
            }

            for (const offset of activeDays) {
                const start = new Date(this.monday);
                start.setDate(this.monday.getDate() + offset);
                start.setHours(h, m, 0, 0);
                events.push({
                    title: task.name,
                    start,
                    end: new Date(start.getTime() + durationMs),
                    color: EVENT_COLOR,
                });
            }
        }

        return events;
    });
}
