import {Component, inject, Input, signal} from '@angular/core';
import {Chapter} from '../../../models/chapter.model';
import {MatIcon} from '@angular/material/icon';
import {DatePipe} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {Task} from '../../../models/task.model';
import {TaskService} from '../../../core/services/task.service';

@Component({
    selector: 'app-arc-view-chapter',
    imports: [
        MatIcon,
        MatIconButton
    ],
    templateUrl: './arc-view-chapter.html',
    styleUrl: './arc-view-chapter.css',
    providers: [DatePipe]
})
export class ArcViewChapter {
    @Input({required: true}) chapter!: Chapter;

    isChapterExpanded = false;
    tasks = signal<Task[]>([]);

    private datePipe = inject(DatePipe);
    private taskService= inject(TaskService);

    formatDate(date: string) {
        return this.datePipe.transform(date, 'MMM d');
    }

    protected expandChapter() {
        this.isChapterExpanded = !this.isChapterExpanded;
        this.taskService.getAllForChapter(this.chapter.id).subscribe(tasks => {
            this.tasks.set(tasks);
        });
    }
}
