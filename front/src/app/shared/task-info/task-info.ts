import {Component, input} from '@angular/core';
import {Tag} from '../../models/tag.model';
import {TagPill} from '../tag-pill/tag-pill';
import {color} from '../../models/tag-colors';

@Component({
    selector: 'app-task-info',
    imports: [TagPill],
    templateUrl: './task-info.html',
    styleUrl: './task-info.css',
})
export class TaskInfo {
    name = input.required<string>();
    summary = input.required<string>();
    tag = input<Tag | null>(null);
    strikethrough = input<boolean>(false);

    protected readonly tagColor = color;
}
