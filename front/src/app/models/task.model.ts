export type TaskStatus = 'PLANNED' | 'IN_PROGRESS' | 'DONE' | 'SKIPPED';

export interface Task {
    id: string;
    chapterId: string;
    estimatedMinutes: number;
    completedMinutes: number;
    startAt: string;
    endAt: string;
    status: TaskStatus;
    name: string;
    description: string;
    tagId: string;
}

export interface TaskCreationDto {
    chapterId: string;
    estimatedMinutes: number;
    scheduledAt: string;
    name: string;
    tagId: string | null;
}

export interface TaskUpdateDto {
    completedMinutes?: number;
    estimatedMinutes?: number;
    scheduledAt?: string;
    status?: TaskStatus;
    name?: string;
    description?: string;
    tagId?: string;
}

export interface TaskCompletedDto {
    completedMinutes: number;
}
