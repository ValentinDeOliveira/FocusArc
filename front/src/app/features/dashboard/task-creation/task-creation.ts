import {Component, EventEmitter, inject, input, Output, output, signal, ViewChild} from '@angular/core';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {MatIcon} from '@angular/material/icon';
import {Tag} from '../../../models/tag.model';
import {TaskRow} from '../../../shared/task-row/task-row';
import {TaskCreationDto} from '../../../models/task.model';
import {TaskService} from '../../../core/services/task.service';
import {ContextStore} from '../../../core/stores/context.store';
import {HttpErrorResponse} from '@angular/common/http';
import {getTaskError} from '../../../models/errors/task-error.model';
import {ToastrService} from 'ngx-toastr';
import {TagSelector} from '../../../shared/tag-selector/tag-selector';
import {TaskNameField} from '../../../shared/input-field/task-name-field/task-name-field';
import {TaskTimeDuration} from '../../../shared/task-time-duration/task-time-duration';
import {ChapterService} from '../../../core/services/chapter.service';
import {ChapterCreationDto} from '../../../models/chapter.model';

@Component({
    selector: 'app-task-creation',
    imports: [
        PrimaryButton,
        MatIcon,
        TaskRow,
        TagSelector,
        TaskNameField,
        TaskTimeDuration,
    ],
    templateUrl: './task-creation.html',
    styleUrl: './task-creation.css',
})
export class TaskCreation {
    @Output() nameChange = new EventEmitter<string>();
    @ViewChild(TaskTimeDuration) taskTimeDuration!: TaskTimeDuration;
    @ViewChild(TaskNameField) taskNameField!: TaskNameField;

    shouldCreateChapter = input.required<boolean>();
    selectedTag = signal<Tag | null>(null);

    taskCreated = output<void>();

    protected isTaskCreation = false;

    toastr = inject(ToastrService);

    private contextStore = inject(ContextStore);
    private taskService = inject(TaskService);
    private chapterService = inject(ChapterService);

    protected addTask() {
        this.isTaskCreation = true;
    }

    protected cancel() {
        this.isTaskCreation = false;
        this.selectedTag.set(null);
        this.taskTimeDuration.reset();
        this.taskNameField.reset();
    }

    protected confirm() {
        const nameValid = this.taskNameField.validate();
        const timeValid = this.taskTimeDuration.validate();
        if (!nameValid || !timeValid) return;

        const today = this.taskTimeDuration.getStartDate();

        console.log(this.shouldCreateChapter());
        if (this.shouldCreateChapter()) {
            const chapterDto: ChapterCreationDto = {
                arcId: this.contextStore.currentArcId()!,
                estimatedMinutes: this.taskTimeDuration.duration(),
                scheduledDate: today.toISOString()
            }

            console.log(chapterDto);

            this.chapterService.create(chapterDto).subscribe(chapter => {
                this.contextStore.setChapterId(chapter.id);
                this.createTask(today);
            })
        } else {
            this.createTask(today);
        }
    }

    private createTask(creationDate: Date) {
        const dto: TaskCreationDto = {
            chapterId: this.contextStore.currentChapterId()!,
            estimatedMinutes: this.taskTimeDuration.duration(),
            scheduledAt: creationDate.toISOString(),
            tagId: !!this.selectedTag ? this.selectedTag()!.id : null,
            name: this.taskNameField.getValue()
        }
        console.log(dto);

        this.taskService.create(dto).subscribe({
            next: () => {
                this.taskCreated.emit();
                this.isTaskCreation = false;
                this.selectedTag.set(null);
            },
            error: (err: HttpErrorResponse) => {
                const message = getTaskError(err.error?.error);
                this.toastr.error(message);
            }
        });
    }
}
