import {Component, inject, OnInit} from '@angular/core';
import {Chapter} from '../../models/chapter.model';
import {ActivatedRoute} from '@angular/router';
import {DatePipe} from '@angular/common';
import {ArcViewChapter} from './arc-view-chapter/arc-view-chapter';
import {ContextStore} from '../../core/stores/context.store';
import {ArcSummaryResponseDto} from '../../models/arc.model';

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
    private contextStore = inject(ContextStore);

    chapters: Chapter[] = [];
    arcSummary: ArcSummaryResponseDto | null = null;
    isOn2Years = false;

    ngOnInit() {
        this.chapters = this.route.snapshot.data['chapters'];
        this.arcSummary = this.contextStore.arcSummary();

        if (this.chapters.length > 1) {
            const c1Year = new Date(this.chapters.at(0)!.scheduledDate).getFullYear();
            const cLastYear = new Date(this.chapters.at(this.chapters.length - 1)!.scheduledDate).getFullYear();
            if (c1Year != cLastYear) {
                this.isOn2Years = true;
            }
        }
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
