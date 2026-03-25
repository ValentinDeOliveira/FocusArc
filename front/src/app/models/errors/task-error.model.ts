export enum TaskError {
    TaskOverlap = 'TaskOverlapException',
}

const taskErrorMessages: Record<TaskError, string> = {
    [TaskError.TaskOverlap]: 'The task is currently overlapping an existing task',
}

export function getTaskError(errorType: string) {
    return taskErrorMessages[errorType as TaskError] ?? 'Something went wrong.';
}
