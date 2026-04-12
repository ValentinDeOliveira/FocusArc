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

    get expandedChapterIndex(): number {
        const today = new Date().toISOString().split('T')[0];
        let lastIndex = 0;

        for (let i = 0; i < this.chapters.length; i++) {
            if (this.chapters[i].scheduledDate <= today) lastIndex = i;
            else break;
        }

        return lastIndex;
    }
}
