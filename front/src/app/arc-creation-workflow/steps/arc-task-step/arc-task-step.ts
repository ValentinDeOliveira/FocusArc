import {Component, computed, inject, output, signal, viewChild} from '@angular/core';
import {FIELD_LIMITS} from '../../../shared/field-limits';
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
import {color} from '../../../models/tag-colors';
import {TagPill} from '../../../shared/tag-pill/tag-pill';
import {TagSelector} from '../../../shared/tag-selector/tag-selector';
import {ContextStore} from '../../../core/stores/context.store';
import {ArcService} from '../../../core/services/arc.service';
import {TaskRecurrenceDto} from '../../../models/task.model';
import {RecurrenceLabel, toRecurrencePayload} from '../../../models/recurrence.model';

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
    styleUrl: './arc-task-step.css'
})
export class ArcTaskStep {
    nextStep = output();
    MAX_NUMBER_OF_TASKS = 5;
    protected readonly FIELD_LIMITS = FIELD_LIMITS;
    protected readonly tagColor = color;

    taskName = signal('');
    tasks = signal<ArcTask[]>([]);
    selectedTag = signal<Tag | null>(null);
    submittedTask = signal(false);
    submitted = signal(false);

    taskNameError = computed(() =>
        this.submittedTask() && !this.taskName().trim() ? 'Name your task' : null
    );

    private recurrence = viewChild.required(ArcTaskRecurrence);
    private toastr = inject(ToastrService);
    private arcService = inject(ArcService);
    private contextStore = inject(ContextStore);

    addTask(): void {
        this.submittedTask.set(true);
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
        this.submittedTask.set(false);
    }

    removeTask(id: string): void {
        this.tasks.update(list => list.filter(t => t.id !== id));
    }

    taskSummary(task: ArcTask): string {
        return [RecurrenceLabel[task.recurrence], task.startTime, `${task.duration} min`].join(' · ');
    }

    goToNextStep() {
        this.submitted.set(true);

        const arcId = this.contextStore.currentArcId();
        if (arcId === null) {
            this.toastr.error('An error occurred.');
            return;
        }

        const dtos: TaskRecurrenceDto[] = this.tasks().map(task => ({
            name: task.name,
            estimatedMinutes: task.duration,
            recurrence: toRecurrencePayload(task),
            scheduledAt: this.toInstant(task.startTime),
            tagId: task.tag?.id,
        }));

        this.arcService.massCreate(arcId!, dtos).subscribe();

        this.nextStep.emit();
    }

    private toInstant(startTime: string): string {
        const [hours, minutes] = startTime.split(':').map(Number);
        const d = new Date();
        d.setHours(hours, minutes, 0, 0);
        return d.toISOString();
    }

    private getTime(startTime: string, duration: number): number[] {
        const [hours, minutes] = startTime.split(':').map(Number);
        const start = hours * 60 + minutes;
        return [start, start + duration];
    }
}
