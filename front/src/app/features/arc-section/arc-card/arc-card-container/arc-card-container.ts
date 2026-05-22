import {Component, computed, input} from '@angular/core';
import {MatCard, MatCardActions} from '@angular/material/card';
import {NgOptimizedImage} from '@angular/common';
import {formatDateShort} from '../../../../utils/date.utils';
import {MatIcon} from '@angular/material/icon';
import {Arc} from '../../../../models/arc.model';
import {PrimaryButton} from '../../../../shared/primary-button/primary-button';

@Component({
    selector: 'app-arc-card-container',
    imports: [
        MatCard,
        MatCardActions,
        NgOptimizedImage,
        MatIcon,
        PrimaryButton,
    ],
    templateUrl: './arc-card-container.html',
    styleUrl: './arc-card-container.css',
})
export class ArcCardContainer {
    arc = input.required<Arc>();
    nbChapterCompleted = input.required<number>();
    nbChapterTotal = input.required<number>();

    protected name = computed(() => this.arc().name);
    protected readonly formatDateShort = formatDateShort;

    get progress(): number {
        if (this.nbChapterCompleted() === 0 || this.nbChapterTotal() === 0) {
            return 0;
        }
        return Math.floor((this.nbChapterCompleted() / this.nbChapterTotal()) * 100);
    }
}
