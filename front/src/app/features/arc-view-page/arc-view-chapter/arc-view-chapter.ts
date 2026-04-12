import {Component, inject, Input, OnInit, signal} from '@angular/core';
import {Chapter} from '../../../models/chapter.model';
import {MatIcon} from '@angular/material/icon';
import {DatePipe} from '@angular/common';
import {MatIconButton} from '@angular/material/button';
import {Task} from '../../../models/task.model';
import {TaskService} from '../../../core/services/task.service';
import {TaskInfo} from '../../../shared/task-info/task-info';
import {TagStore} from '../../../core/stores/tag.store';

@Component({
    selector: 'app-arc-view-chapter',
    imports: [
        MatIcon,
        MatIconButton,
        TaskInfo
    ],
    templateUrl: './arc-view-chapter.html',
    styleUrl: './arc-view-chapter.css',
    providers: [DatePipe]
})
export class ArcViewChapter implements OnInit {
    @Input({required: true}) chapter!: Chapter;
    @Input() isChapterExpanded: boolean = false;

    tagStore = inject(TagStore);
    tasks = signal<Task[]>([]);

    private datePipe = inject(DatePipe);
    private taskService= inject(TaskService);
    private isOn2Years = false;

    ngOnInit() {
        if (this.isChapterExpanded) {
            this.getAllChapters();
        }
    }

    formatDate(date: string) {
        // TODO move this logic to another file?
        if (this.isOn2Years) {
            return this.datePipe.transform(date, 'MMMM d yyyy');
        }

        return this.datePipe.transform(date, 'MMMM d');
    }

    protected expandChapter() {
        this.isChapterExpanded = !this.isChapterExpanded;

        if (this.tasks().length > 0) {
            return;
        }

        this.getAllChapters();
    }

    private getAllChapters() {
        this.taskService.getAllForChapter(this.chapter.id).subscribe(tasks => {
            this.tasks.set(tasks);
            // TODO move this to the store?
            if (tasks.length > 1) {
                const t1Year = new Date(tasks.at(0)!.startAt).getFullYear();
                const tLastYear = new Date(tasks.at(tasks.length - 1)!.startAt).getFullYear();
                if (t1Year != tLastYear) {
                    this.isOn2Years = true;
                }
            }
        });
    }
}
