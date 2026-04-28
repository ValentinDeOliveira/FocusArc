import {RecurrenceType} from './arc-task-recurrence/arc-task-recurrence';

export interface ArcTask {
    id: string;
    name: string;
    tag?: string;
    recurrence: RecurrenceType;
    daysOfWeek?: string[];
    everyNDays?: number;
    startTime: string;
    duration: number;
}