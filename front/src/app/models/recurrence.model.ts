import {ArcTask} from '../shared/arc-task.model';

export enum RecurrenceType {
    DAILY = 'DAILY',
    DAYS_OF_WEEK = 'DAYS_OF_WEEK',
    EVERY_N_DAYS = 'EVERY_N_DAYS',
}

export const RecurrenceLabel: Record<RecurrenceType, string> = {
    [RecurrenceType.DAILY]: 'Daily',
    [RecurrenceType.DAYS_OF_WEEK]: 'Days of week',
    [RecurrenceType.EVERY_N_DAYS]: 'Every N Days',
};

export type TaskRecurrencePayload =
    | { type: RecurrenceType.DAILY }
    | { type: RecurrenceType.EVERY_N_DAYS; n: number }
    | { type: RecurrenceType.DAYS_OF_WEEK; daysOfWeek: string[] };

export function toRecurrencePayload(task: ArcTask): TaskRecurrencePayload {
    switch (task.recurrence) {
        case RecurrenceType.DAILY:        return { type: RecurrenceType.DAILY };
        case RecurrenceType.EVERY_N_DAYS: return { type: RecurrenceType.EVERY_N_DAYS, n: task.everyNDays! };
        case RecurrenceType.DAYS_OF_WEEK: return { type: RecurrenceType.DAYS_OF_WEEK, daysOfWeek: task.daysOfWeek! };
    }
}