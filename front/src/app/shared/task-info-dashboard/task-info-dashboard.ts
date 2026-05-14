import {formatMinutes} from '../../utils/time.utils';
import {Component, computed, inject, input} from '@angular/core';
import {TagStore} from '../../core/stores/tag.store';
import {Task} from '../../models/task.model';
import {formatTimeHHmm} from '../../utils/date.utils';
import {TaskInfo} from '../task-info/task-info';
import {isTaskEnded} from '../../utils/task.utils';
import {TaskStatusBadge} from '../task-status-badge/task-status-badge';

@Component({
    selector: 'app-task-info-dashboard',
    imports: [TaskInfo, TaskStatusBadge],
    templateUrl: './task-info-dashboard.html',
    styleUrl: './task-info-dashboard.css',
})
export class TaskInfoDashboard {
    private tagStore = inject(TagStore);

    task = input.required<Task>();
    tag = computed(() => {
        return this.tagStore.byId(this.task().tagId);
    });
    strikethrough = computed(() => {
        return isTaskEnded(this.task());
    });

    protected get taskSummary() {
        return [
            formatTimeHHmm(this.task().startAt) + ' - ' + formatTimeHHmm(this.task().endAt),
            formatMinutes(this.task().estimatedMinutes)
        ].join(' · ');
    }
}

