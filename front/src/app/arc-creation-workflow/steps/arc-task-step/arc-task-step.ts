import {Component, inject, output, signal, viewChild} from '@angular/core';
import {ArcCreationStepper} from '../../arc-creation-stepper/arc-creation-stepper';
import {CardPageLayout} from '../../../shared/card-page-layout/card-page-layout';
import {MatIcon} from '@angular/material/icon';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {ArcTaskRecurrence} from '../../../shared/arc-task-recurrence/arc-task-recurrence';
import {InputField} from '../../../shared/input-field/input-field';
import {WeekSchedule} from '../../../shared/week-schedule/week-schedule';
import {ArcTask} from '../../../shared/arc-task.model';
import {ToastrService} from 'ngx-toastr';
import {Tag} from '../../../models/tag.model';
import {TagPill} from '../../../shared/tag-pill/tag-pill';
import {TagSelector} from '../../../shared/tag-selector/tag-selector';

@Component({
    selector: 'app-arc-task-step',
    imports: [
        ArcCreationStepper,
        CardPageLayout,
        MatIcon,
        PrimaryButton,
        ArcTaskRecurrence,
        InputField,
        WeekSchedule,
        TagPill,
        TagSelector,
    ],
    templateUrl: './arc-task-step.html',
    styleUrls: [
        './arc-task-step.css',
        '../../../shared/form-shared.css'
    ]
})
export class ArcTaskStep {
    nextStep = output();
    MAX_NUMBER_OF_TASKS = 5;

    taskName = signal('');
    tasks = signal<ArcTask[]>([]);
    selectedTag = signal<Tag | null>(null);

    private recurrence = viewChild.required(ArcTaskRecurrence);
    private toastr = inject(ToastrService);

    addTask(): void {
        const name = this.taskName().trim();
        if (!name) return;

        const [newTaskStart, newTaskEnd] = this.getTime(this.recurrence().startTime(), this.recurrence().duration());
        for (const task of this.tasks()) {
            if (task.name === name) {
                this.toastr.error('Another task have the same name.');
                return;
            }
            const [taskStart, taskEnd] = this.getTime(task.startTime, task.duration);
            if (taskStart < newTaskEnd && newTaskStart < taskEnd) {
                this.toastr.error(`The task overlaps existing task '${task.name}'.`);
                return;
            }
        }

        const config = this.recurrence().config();
        this.tasks.update(list => [...list, {
            id: crypto.randomUUID(),
            name,
            tag: this.selectedTag() ?? undefined,
            ...config
        }]);
        this.taskName.set('');
        this.selectedTag.set(null);
        this.recurrence().reset();
    }

    removeTask(id: string): void {
        this.tasks.update(list => list.filter(t => t.id !== id));
    }

    taskSummary(task: ArcTask): string {
        return [task.recurrence, task.startTime, `${task.duration} min`].join(' · ');
    }

    private getTime(startTime: string, duration: number): number[] {
        const [hours, minutes] = startTime.split(':').map(Number);
        const start = hours * 60 + minutes;
        return [start, start + duration];
    }
}