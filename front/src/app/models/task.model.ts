export enum TaskStatus {
    PLANNED = 'PLANNED',
    IN_PROGRESS = 'IN_PROGRESS',
    DONE = 'DONE',
    SKIPPED = 'SKIPPED'
}

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
