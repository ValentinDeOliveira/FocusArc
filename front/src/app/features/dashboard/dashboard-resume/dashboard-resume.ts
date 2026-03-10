import {Component, inject} from '@angular/core';
import {TaskService} from '../../../core/services/task.service';
import {Task} from '../../../models/task.model';
import {DashboardTask} from '../dashboard-task/dashboard-task';
import {toSignal} from '@angular/core/rxjs-interop';

@Component({
    selector: 'app-dashboard-resume',
    imports: [
        DashboardTask
    ],
    templateUrl: './dashboard-resume.html',
    styleUrl: './dashboard-resume.css',
})
export class DashboardResume {
    private taskService = inject(TaskService);
    tasks = toSignal(this.taskService.getTodayTask(), { initialValue: [] as Task[] });
}
