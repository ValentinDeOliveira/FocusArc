import {Component, Input} from '@angular/core';
import {Arc} from '../../../../models/arc.model';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';

@Component({
    selector: 'app-arc-progress-stats',
    imports: [
        MatCard,
        MatCardContent,
        MatIcon
    ],
    templateUrl: './arc-progress-stats.html',
    styleUrl: './arc-progress-stats.css',
})
export class ArcProgressStats {
    @Input({ required: true }) arc!: Arc;
}
