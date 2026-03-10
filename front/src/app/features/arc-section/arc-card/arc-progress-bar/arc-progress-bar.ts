import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-arc-progress-bar',
    imports: [],
    templateUrl: './arc-progress-bar.html',
    styleUrl: './arc-progress-bar.css',
})
export class ArcProgressBar {
    @Input() progress: number = 0;
}
