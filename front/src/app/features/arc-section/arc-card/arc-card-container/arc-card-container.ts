import {Component, Input, OnInit} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {NgOptimizedImage} from '@angular/common';
import {formatDateShort} from '../../../../utils/date.utils';
import {ArcProgressBar} from '../arc-progress-bar/arc-progress-bar';
import {MatIcon} from '@angular/material/icon';
import {Arc} from '../../../../models/arc.model';
import {PrimaryButton} from '../../../../shared/primary-button/primary-button';

@Component({
    selector: 'app-arc-card-container',
    imports: [
        MatCard,
        MatCardContent,
        MatCardActions,
        NgOptimizedImage,
        ArcProgressBar,
        MatIcon,
        PrimaryButton,
    ],
    templateUrl: './arc-card-container.html',
    styleUrl: './arc-card-container.css',
})
export class ArcCardContainer implements OnInit {
    @Input({ required: true }) arc!: Arc;
    @Input({ required: true }) nbChapterCompleted!: number;
    @Input({ required: true }) nbChapterTotal!: number;
    name!: string;
    protected readonly formatDateShort = formatDateShort;

    ngOnInit(): void {
        this.name = this.arc.name + ' Arc';
    }

    get progress(): number {
        if (this.nbChapterCompleted == 0 || this.nbChapterTotal === 0) {
            return 0;
        }

        return Math.floor((this.nbChapterCompleted / this.nbChapterTotal) * 100);
    }
}
