export type TaskStatus = 'PLANNED' | 'IN_PROGRESS' | 'DONE' | 'SKIPPED';

export interface Task {
    id: string;
    chapter: string;
    estimatedMinutes: number;
    completedMinutes: number;
    scheduledDate: string;
    status: TaskStatus;
}

export interface TaskCreationDto {
    chapterId: string;
    estimatedMinutes: number;
    scheduledAt: string;
}

export interface TaskUpdateDto {
    completedMinutes: number;
    estimatedMinutes: number;
    scheduledAt: string;
    status: TaskStatus;
}

export interface TaskCompletedDto {
    completedMinutes: number;
}
