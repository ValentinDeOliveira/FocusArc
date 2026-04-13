import {Injectable, signal} from '@angular/core';
import {ArcSummaryResponseDto} from '../../models/arc.model';

@Injectable({ providedIn: 'root' })
export class ContextStore {
    private arcId = signal<string | null>(null);
    private chapterId = signal<string | null>(null);
    private arcSummarySignal = signal<ArcSummaryResponseDto | null>(null);

    arcSummary = this.arcSummarySignal.asReadonly();
    currentChapterId = this.chapterId.asReadonly();
    currentArcId = this.arcId.asReadonly();

    setArcId(arcId: string) {
        this.arcId.set(arcId);
    }

    setChapterId(chapterId: string) {
        this.chapterId.set(chapterId);
    }

    setSummary(summary: ArcSummaryResponseDto): void {
        this.arcSummarySignal.set(summary);
    }
}
