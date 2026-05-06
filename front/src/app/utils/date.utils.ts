import {formatDate} from '@angular/common';

export enum DayOfWeek {
    MONDAY = 'MONDAY', TUESDAY = 'TUESDAY', WEDNESDAY = 'WEDNESDAY',
    THURSDAY = 'THURSDAY', FRIDAY = 'FRIDAY', SATURDAY = 'SATURDAY', SUNDAY = 'SUNDAY',
}
// used to convert to Java DayOfWeek format
export const DAY_ABBREV_TO_ENUM: Record<string, DayOfWeek> = {
    Mon: DayOfWeek.MONDAY, Tue: DayOfWeek.TUESDAY, Wed: DayOfWeek.WEDNESDAY,
    Thu: DayOfWeek.THURSDAY, Fri: DayOfWeek.FRIDAY, Sat: DayOfWeek.SATURDAY,
    Sun: DayOfWeek.SUNDAY,
};

export const DAY_OF_WEEK_INDEX: Record<DayOfWeek, number> = {
    [DayOfWeek.MONDAY]: 0, [DayOfWeek.TUESDAY]: 1, [DayOfWeek.WEDNESDAY]: 2,
    [DayOfWeek.THURSDAY]: 3, [DayOfWeek.FRIDAY]: 4, [DayOfWeek.SATURDAY]: 5,
    [DayOfWeek.SUNDAY]: 6,
};

export function getWeekStart(from = new Date()): Date {
    const d = new Date(from);
    const day = d.getDay();
    d.setDate(d.getDate() + (day === 0 ? -6 : 1 - day));
    d.setHours(0, 0, 0, 0);
    return d;
}

export function isAfter(date: Date, reference: Date): boolean {
    return date > reference;
}

// 'MMMM d yyyy' → "January 5 2025"
export function formatDateFull(date: Date | string): string {
    return formatDate(date, 'MMMM d yyyy', 'en-US');
}

// 'MMMM d' → "January 5"
export function formatDateLong(date: Date | string | number): string {
    return formatDate(date, 'MMMM d', 'en-US');
}

// 'MMM d' → "Jan 5"
export function formatDateShort(date: Date | string): string {
    return formatDate(date, 'MMM d', 'en-US');
}

// 'HH:mm' → "09:30"
export function formatTimeHHmm(date: Date | string): string {
    return formatDate(date, 'HH:mm', 'en-US');
}
