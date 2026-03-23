import {Injectable, signal} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ContextStore {
    private arcId = signal<string | null>(null);
    private chapterId = signal<string | null>(null);

    currentArcId = this.arcId.asReadonly();
    currentChapterId = this.chapterId.asReadonly();

    setArcId(arcId: string) {
        this.arcId.set(arcId);
    }

    setChapterId(chapterId: string) {
        this.chapterId.set(chapterId);
    }
}
