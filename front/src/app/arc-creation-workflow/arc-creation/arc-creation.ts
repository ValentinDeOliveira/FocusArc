import {Component, signal} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ArcDetailsStep} from '../steps/arc-details-step/arc-details-step';
import {ArcTaskStep} from '../steps/arc-task-step/arc-task-step';

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
}
