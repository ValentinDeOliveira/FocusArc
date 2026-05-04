import {Component, inject, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ArcDetailsStep} from '../steps/arc-details-step/arc-details-step';
import {ArcTaskStep} from '../steps/arc-task-step/arc-task-step';
import {Router} from '@angular/router';

@Component({
    selector: 'app-arc-creation',
    imports: [
        FormsModule,
        ReactiveFormsModule,
        ArcDetailsStep,
        ArcTaskStep,
    ],
    templateUrl: './arc-creation.html',
})
export class ArcCreation {
    currentStep = signal(1);

    private router = inject(Router);

    goToDashboard() {
        this.router.navigate(['/']);
    }
}
