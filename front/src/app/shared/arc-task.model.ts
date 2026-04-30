import {RecurrenceType} from './arc-task-recurrence/arc-task-recurrence';
import {Tag} from '../models/tag.model';

export interface ArcTask {
    id: string;
    name: string;
    tag?: Tag;
    recurrence: RecurrenceType;
    daysOfWeek?: string[];
    everyNDays?: number;
    startTime: string;
    duration: number;
}
