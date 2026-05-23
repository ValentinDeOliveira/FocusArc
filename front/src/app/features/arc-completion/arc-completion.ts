import {Component, computed, inject, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {NgOptimizedImage} from '@angular/common';
import {formatMinutes} from '../../utils/time.utils';
import {formatDateShort} from '../../utils/date.utils';
import {ArcTagStats} from '../arc-view-page/arc-tag-stats/arc-tag-stats';
import {PrimaryButton} from '../../shared/primary-button/primary-button';
import {StatTile} from '../../shared/stat-tile/stat-tile';
import {ArcCompletionData} from './arc-completion.resolver';

@Component({
    selector: 'app-arc-completion',
    imports: [ArcTagStats, PrimaryButton, StatTile, NgOptimizedImage],
    templateUrl: './arc-completion.html',
    styleUrl: './arc-completion.css',
})
export class ArcCompletion {
    private data = inject(ActivatedRoute).snapshot.data['completion'] as ArcCompletionData;

    arc = signal(this.data.arc);
    arcSummary = signal(this.data.summary);
    tagStats = signal(this.data.tagStats);

    weeks = computed(() => Math.round(
        (new Date(this.arc().endDate).getTime() - new Date(this.arc().startDate).getTime()) / (1000 * 60 * 60 * 24 * 7)
    ));

    durationDays = computed(() => Math.round(
        (new Date(this.arc().endDate).getTime() - new Date(this.arc().startDate).getTime()) / (1000 * 60 * 60 * 24)
    ) + 1);

    totalChapters = computed(() => {
        const s = this.arcSummary();
        return s.nbChapterCompleted + s.nbChapterPlanned + s.nbChapterSkipped;
    });

    overallCompletion = computed(() =>
        this.totalChapters() > 0
            ? Math.round((this.arcSummary().nbChapterCompleted / this.totalChapters()) * 100)
            : 0
    );

    focusTime = computed(() => formatMinutes(this.arcSummary().totalCompletedMinutes) || '-');

    totalTasksDone = computed(() => this.tagStats().reduce((sum, s) => sum + s.done, 0));
    totalTasksPlanned = computed(() => this.tagStats().reduce((sum, s) => sum + s.total, 0));

    taskCompletionPct = computed(() =>
        this.totalTasksPlanned() === 0 ? 0
            : Math.round((this.totalTasksDone() / this.totalTasksPlanned()) * 100)
    );

    descriptionText = computed(() => {
        return `You wrapped up ${this.arcSummary().nbChapterCompleted} of ${this.totalChapters()} chapters and logged real focus time. Take a moment, then plan what's next.`;
    });

    dateRange= computed(() => {
        const start= formatDateShort(this.arc().startDate).toUpperCase();
        const end= formatDateShort(this.arc().endDate).toUpperCase();
        return `${start} → ${end} · ${this.weeks()} WEEKS`;
    });
}
