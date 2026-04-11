import {Component, inject, OnInit} from '@angular/core';
import {Chapter} from '../../models/chapter.model';
import {ActivatedRoute} from '@angular/router';
import {DatePipe} from '@angular/common';
import {ArcViewChapter} from './arc-view-chapter/arc-view-chapter';

@Component({
    selector: 'app-arc-view-page',
    templateUrl: './arc-view-page.html',
    styleUrl: './arc-view-page.css',
    imports: [
        ArcViewChapter
    ],
    providers: [DatePipe]
})
export class ArcViewPage implements OnInit {
    private route = inject(ActivatedRoute);

    chapters: Chapter[] = [];

    ngOnInit() {
        this.chapters = this.route.snapshot.data['chapters'];
    }
}
