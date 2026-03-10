import {Component, Input, OnInit} from '@angular/core';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {NgOptimizedImage} from '@angular/common';
import {ArcProgressBar} from '../arc-progress-bar/arc-progress-bar';
import {MatIcon} from '@angular/material/icon';
import {Arc} from '../../../models/arc.model';

@Component({
    selector: 'app-arc-card',
    imports: [
        MatCard,
        MatCardContent,
        MatCardActions,
        NgOptimizedImage,
        ArcProgressBar,
        MatIcon
    ],
    templateUrl: './arc-card.html',
    styleUrl: './arc-card.css',
})
export class ArcCard implements OnInit {
    @Input({ required: true }) arc!: Arc;

    name!: string;
    nbTotalChapters: number = 30;
    currentChapter: number = 18;
    progress!: number;

    ngOnInit(): void {
        this.name = this.arc.name + ' Arc';
        this.progress = Math.floor((this.currentChapter / this.nbTotalChapters) * 100);
    }
}
