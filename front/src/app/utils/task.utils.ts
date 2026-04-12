import {Task} from '../models/task.model';

export function isTaskEnded(task: Task): boolean {
    return task.status === "DONE" || task.status === "SKIPPED";
}
