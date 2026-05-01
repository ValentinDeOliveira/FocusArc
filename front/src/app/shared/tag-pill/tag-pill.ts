import {Component, Input} from '@angular/core';
import {color, Tag} from '../../models/tag.model';
import {TagDot} from '../tag-dot/tag-dot';

@Component({
    selector: 'app-tag-pill',
    imports: [
        TagDot
    ],
    templateUrl: './tag-pill.html',
    styleUrl: './tag-pill.css',
})
export class TagPill {
    @Input() tag!: Tag;
    protected readonly color = color;
}
