import { Component } from '@angular/core';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {NgOptimizedImage} from '@angular/common';
import {MatProgressBar} from '@angular/material/progress-bar';
import {ArcProgressBar} from '../arc-progress-bar/arc-progress-bar';
import {MatButton, MatFabButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
    selector: 'app-arc-card',
    imports: [
        MatCard,
        MatCardContent,
        MatCardActions,
        NgOptimizedImage,
        ArcProgressBar,
        MatButton,
        MatIcon,
        MatFabButton
    ],
    templateUrl: './arc-card.html',
    styleUrl: './arc-card.css',
})
export class ArcCard {}
