import {Component, Input} from '@angular/core';
import {Arc, ArcSummaryResponseDto} from '../../../../models/arc.model';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';

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
    @Input({ required: true }) arc!: Arc;
    @Input({ required: true }) arcSummary!: ArcSummaryResponseDto;

    getRemainingDays() {
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        const end = new Date(this.arc.endDate);
        end.setHours(0, 0, 0, 0);

        const diff = end.getTime() - now.getTime();
        return Math.floor(diff / (1000 * 60 * 60 * 24));
    }

    getCompletedTime() {
        const hours = Math.floor(this.arcSummary.totalCompletedMinutes / 60);
        const minutes = this.arcSummary.totalCompletedMinutes % 60;
        return `${hours}h ${minutes}m`;
    }
}
