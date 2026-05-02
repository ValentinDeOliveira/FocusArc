import {Component, Input} from '@angular/core';
import {Tag} from '../../models/tag.model';
import {color, colorBg} from '../../models/tag-colors';

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
    protected readonly colorBg = colorBg;
}
