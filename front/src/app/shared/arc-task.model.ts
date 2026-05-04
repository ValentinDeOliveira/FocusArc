import {Tag} from '../models/tag.model';
import {RecurrenceType} from '../models/recurrence.model';

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
