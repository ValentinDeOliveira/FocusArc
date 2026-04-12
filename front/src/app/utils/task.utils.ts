import {Task, TaskStatus} from '../models/task.model';

export function isTaskEnded(task: Task): boolean {
    return task.status === TaskStatus.DONE || task.status === TaskStatus.SKIPPED;
}
