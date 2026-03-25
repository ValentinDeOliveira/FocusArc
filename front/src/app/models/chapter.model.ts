import {Task} from './task.model';

export interface Chapter {
    id: string;
    arc: string;
    estimatedMinutes: number;
    completedMinutes: number;
    scheduledDate: string;
}

export interface ChapterCreationDto {
    arcId: string;
    estimatedMinutes: number;
    scheduledDate: string;
}

export interface ChapterUpdateDto {
    scheduledDate: string;
}

export interface ChapterSummaryResponseDto {
    chapterId: string;
    tasksToComplete: Task[];
    estimatedMinutes: number;
    completedMinutes: number;
    remainingTime: number;
}
