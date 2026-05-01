import {Component, Input} from '@angular/core';
import {color, Tag} from '../../models/tag.model';


@Component({
    selector: 'app-tag-dot',
    templateUrl: './tag-dot.html',
    styleUrl: './tag-dot.css',
})
export class TagDot {
    @Input() tag?: Tag | null;
    protected readonly color = color;
}
