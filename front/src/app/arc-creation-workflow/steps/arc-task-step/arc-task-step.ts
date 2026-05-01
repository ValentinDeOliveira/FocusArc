import {Component, inject, OnInit, output, signal, viewChild} from '@angular/core';
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
import {TagService} from '../../../core/services/tag.service';
import {TagPill} from '../../../shared/tag-pill/tag-pill';

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
    ],
    templateUrl: './arc-task-step.html',
    styleUrls: [
        './arc-task-step.css',
        '../../../shared/form-shared.css'
    ]
})
export class ArcTaskStep implements OnInit {
    nextStep = output();
    MAX_NUMBER_OF_TASKS = 5;

    taskName = signal('');
    taskTag = signal('');
    tasks = signal<ArcTask[]>([]);

    private recurrence = viewChild.required(ArcTaskRecurrence);
    private tags = signal<Tag[]>([]);

    private tagService = inject(TagService);

    toastr = inject(ToastrService);

    ngOnInit() {
        this.tagService.getAllForUser().subscribe(tags => {
            this.tags.set(tags);
        });
    }

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

        if (this.taskTag().trim() !== '') {
            this.tagService.create({ label: this.taskTag(), color: "RED" }).subscribe(tag => {
                this.tags.update(list => [...list, tag]);
                this.pushTask(name, config, tag);
            });
        } else {
            this.pushTask(name, config, undefined);
        }
    }

    private pushTask(name: string, config: Omit<ArcTask, 'id' | 'name' | 'tag'>, tag: Tag | undefined): void {
        this.tasks.update(list => [...list, { id: crypto.randomUUID(), name, tag, ...config }]);
        this.taskName.set('');
        this.taskTag.set('');
        this.recurrence().reset();
    }

    removeTask(id: string): void {
        this.tasks.update(list => list.filter(t => t.id !== id));
    }

    taskSummary(task: ArcTask): string {
        const parts = [task.recurrence, task.startTime, `${task.duration} min`];
        return parts.join(' · ');
    }

    getTime(startTime: string, duration: number): number[] {
        const [hours, minutes] = startTime.split(':').map(Number);
        const taskStart = hours * 60 + minutes;
        const taskEnd = taskStart + duration;

        return [taskStart, taskEnd];
    }
}
