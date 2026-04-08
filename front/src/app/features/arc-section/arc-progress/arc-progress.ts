import {Component, inject, OnInit, signal} from '@angular/core';
import {ArcCardContainer} from '../arc-card/arc-card-container/arc-card-container';
import {ArcService} from '../../../core/services/arc.service';
import {Arc, ArcSummaryResponseDto} from '../../../models/arc.model';
import {AuthService} from '../../../core/services/auth.service';
import {LoginRequestDto} from '../../../models/auth.model';
import {ArcProgressStats} from '../arc-progress-card/arc-progress-stats/arc-progress-stats';
import {ContextStore} from '../../../core/stores/context.store';

@Component({
    selector: 'app-arc-progress',
    imports: [
        ArcCardContainer,
        ArcProgressStats
    ],
    templateUrl: './arc-progress.html',
    styleUrl: './arc-progress.css',
})
export class ArcProgress implements OnInit {
    private arcService = inject(ArcService);
    private authService = inject(AuthService);
    contextStore = inject(ContextStore);

    arc = signal<Arc | undefined>(undefined);

    ngOnInit(): void {
        const dto: LoginRequestDto = {
            email: 'dev@focusarc.com',
            password: 'password123',
        }

        this.authService.login(dto).subscribe(response => {
            localStorage.setItem('token', response.accessToken);
            this.arcService.getAll().subscribe(arcs => {
                this.arc.set(Object.values(arcs).at(0));
            });
            this.arcService.getSummary().subscribe(summary => {
                this.contextStore.setSummary(summary);
                this.contextStore.setArcId(summary.arcId);
            });
        });
    }

    // TODO fix when modifying the summary endpoint to get total chapters from back
    getNbTotalChapter() {
        return this.contextStore.arcSummary()!.nbChapterCompleted + this.contextStore.arcSummary()!.nbChapterSkipped +
            this.contextStore.arcSummary()!.nbChapterPlanned;
    }
}
