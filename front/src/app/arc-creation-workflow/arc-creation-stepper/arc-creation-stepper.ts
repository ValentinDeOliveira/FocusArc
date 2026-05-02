import { Component, input } from '@angular/core';

@Component({
    selector: 'app-arc-creation-stepper',
    imports: [],
    templateUrl: './arc-creation-stepper.html',
    styleUrl: './arc-creation-stepper.css',
})
export class ArcCreationStepper {
    activeStep = input<number>(1);

    readonly steps = ['Your Arc', 'Routine tasks'];
}