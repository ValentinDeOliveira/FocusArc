import {Component, inject, Input, OnInit} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {ArcProgressBar} from '../arc-progress-bar/arc-progress-bar';
import {MatIcon} from '@angular/material/icon';
import {Arc} from '../../../../models/arc.model';

@Component({
    selector: 'app-arc-card-container',
    imports: [
        MatCard,
        MatCardContent,
        MatCardActions,
        NgOptimizedImage,
        ArcProgressBar,
        MatIcon
    ],
    providers: [DatePipe],
    templateUrl: './arc-card-container.html',
    styleUrl: './arc-card-container.css',
})
export class ArcCardContainer implements OnInit {
    @Input({ required: true }) arc!: Arc;
    @Input({ required: true }) nbChapterCompleted!: number;
    @Input({ required: true }) nbChapterTotal!: number;
    private datePipe = inject(DatePipe);

    name!: string;
    progress!: number;

    ngOnInit(): void {
        this.name = this.arc.name + ' Arc';
        this.progress = Math.floor((this.nbChapterCompleted / this.nbChapterTotal) * 100);
    }

    formatDate(date: string) {
        return this.datePipe.transform(date, 'MMM d');
    }


}
