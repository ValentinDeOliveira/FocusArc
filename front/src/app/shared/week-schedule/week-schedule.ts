import {Component, computed, input} from '@angular/core';
import {CalendarDateFormatter, CalendarEvent, CalendarWeekViewComponent, DateFormatterParams} from 'angular-calendar';
import {ArcTask} from '../arc-task.model';
import {eventColor} from '../../models/tag-colors';
import {RecurrenceType} from '../../models/recurrence.model';
import {DAY_OF_WEEK_INDEX, DayOfWeek, getWeekStart} from '../../utils/date.utils';
import {parseTime} from '../../utils/time.utils';

class WeekScheduleDateFormatter extends CalendarDateFormatter {
    override weekViewColumnHeader({date, locale}: DateFormatterParams): string {
        return new Intl.DateTimeFormat(locale, {weekday: 'short'}).format(date).toUpperCase();
    }

    override weekViewColumnSubHeader({date, locale}: DateFormatterParams): string {
        return new Intl.DateTimeFormat(locale, {day: 'numeric'}).format(date);
    }
}



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

    private readonly monday = getWeekStart();

    calendarEvents = computed<CalendarEvent[]>(() => {
        const events: CalendarEvent[] = [];

        for (const task of this.tasks()) {
            const [h, m] = parseTime(task.startTime);
            const durationMs = task.duration * 60 *  1000;

            for (const offset of this.getActiveDays(task)) {
                const start = new Date(this.monday);
                start.setDate(this.monday.getDate() + offset);
                start.setHours(h, m, 0, 0);
                events.push({
                    title: task.name,
                    start,
                    end: new Date(start.getTime() + durationMs),
                    color: eventColor(task.tag?.color),
                });
            }
        }
        return events;
    });

    private getActiveDays(task: ArcTask): number[] {
        switch (task.recurrence) {
            case RecurrenceType.DAILY:
                return [0, 1, 2, 3, 4, 5, 6];
            case RecurrenceType.DAYS_OF_WEEK:
                return (task.daysOfWeek ?? []).map(d => DAY_OF_WEEK_INDEX[d as DayOfWeek]).filter(d => d !== undefined);
            case RecurrenceType.EVERY_N_DAYS:
                const activeDays = [];
                const n = task.everyNDays ?? 2;
                for (let i = 0; i < 7; i += n) activeDays.push(i);
                return activeDays;
            default:
                const _: never = task.recurrence;
                throw new Error(`Unhandled recurrence type: ${_}`);
        }
    }
}
