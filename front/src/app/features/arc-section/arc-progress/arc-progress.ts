import { Component } from '@angular/core';
import {ArcCard} from '../arc-card/arc-card';

@Component({
    selector: 'app-arc-progress',
    imports: [
        ArcCard
    ],
    templateUrl: './arc-progress.html',
    styleUrl: './arc-progress.css',
})
export class ArcProgress {}
