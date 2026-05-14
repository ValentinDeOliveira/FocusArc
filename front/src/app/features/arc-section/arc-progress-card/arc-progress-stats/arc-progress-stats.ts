import {Component, input} from '@angular/core';
import {Arc, ArcSummaryResponseDto} from '../../../../models/arc.model';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {formatMinutes} from '../../../../utils/time.utils';

@Component({
    selector: 'app-arc-progress-stats',
    imports: [
        MatCard,
        MatCardContent,
        MatIcon
    ],
    templateUrl: './arc-progress-stats.html',
    styleUrl: './arc-progress-stats.css',
})
export class ArcProgressStats {
    arc = input.required<Arc>();
    arcSummary = input.required<ArcSummaryResponseDto>();

    get remainingDays() {
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        const end = new Date(this.arc().endDate);
        end.setHours(0, 0, 0, 0);

        const diff = end.getTime() - now.getTime();
        return Math.floor(diff / (1000 * 60 * 60 * 24));
    }

    get shouldDisplayCompletedTime(): boolean {
        return this.arcSummary().totalCompletedMinutes > 0;
    }

    get completedTime() {
        return formatMinutes(this.arcSummary().totalCompletedMinutes);
    }
}
