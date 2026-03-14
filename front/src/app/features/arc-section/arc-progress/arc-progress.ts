import {Component, inject, OnInit, signal} from '@angular/core';
import {ArcCardContainer} from '../arc-card/arc-card-container/arc-card-container';
import {ArcService} from '../../../core/services/arc.service';
import {Arc, ArcSummaryResponseDto} from '../../../models/arc.model';
import {AuthService} from '../../../core/services/auth.service';
import {LoginRequestDto} from '../../../models/auth.model';
import {ArcProgressStats} from '../arc-progress-card/arc-progress-stats/arc-progress-stats';

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
    arc = signal<Arc | undefined>(undefined);
    arcSummary = signal<ArcSummaryResponseDto | undefined>(undefined);

    ngOnInit(): void {
        const dto: LoginRequestDto = {
            email: 'alice@example.com',
            password: 'password123',
        }

        this.authService.login(dto).subscribe(response => {
            localStorage.setItem('token', response.accessToken);
            this.arcService.getAll().subscribe(arcs => {
                this.arc.set(Object.values(arcs).at(0));
            });
            this.arcService.getSummary().subscribe(summary => {
                this.arcSummary.set(summary);
            });
        });
    }

    // TODO fix when modifying the summary endpoint to get total chapters from back
    getNbTotalChapter() {
        return this.arcSummary()!.nbChapterCompleted + this.arcSummary()!.nbChapterSkipped +
            this.arcSummary()!.nbChapterPlanned;
    }
}
