import {Component, inject, OnInit} from '@angular/core';
import {Chapter} from '../../models/chapter.model';
import {ActivatedRoute} from '@angular/router';
import {DatePipe} from '@angular/common';
import {ArcViewChapter, ChapterState} from './arc-view-chapter/arc-view-chapter';
import {ContextStore} from '../../core/stores/context.store';
import {ArcSummaryResponseDto} from '../../models/arc.model';
import {ArcDonutChart} from './arc-donut-chart/arc-donut-chart';

@Component({
    selector: 'app-arc-view-page',
    templateUrl: './arc-view-page.html',
    styleUrl: './arc-view-page.css',
    imports: [
        ArcViewChapter,
        ArcDonutChart,
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

    get chapterCounts(): Record<ChapterState, number> {
        const today = new Date().toISOString().split('T')[0];
        const counts: Record<ChapterState, number> = {
            [ChapterState.DONE]: 0,
            [ChapterState.INCOMPLETE]: 0,
            [ChapterState.IN_PROGRESS]: 0,
            [ChapterState.PLANNED]: 0,
        };

        for (const chapter of this.chapters) {
            if (chapter.allTasksDone) counts[ChapterState.DONE]++;
            else if (chapter.scheduledDate < today) counts[ChapterState.INCOMPLETE]++;
            else if (chapter.scheduledDate === today) counts[ChapterState.IN_PROGRESS]++;
            else counts[ChapterState.PLANNED]++;
        }

        return counts;
    }
}
