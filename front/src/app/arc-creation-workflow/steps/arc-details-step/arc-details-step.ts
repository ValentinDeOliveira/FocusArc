import {Component, computed, effect, output, signal, untracked} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {CardPageLayout} from '../../../shared/card-page-layout/card-page-layout';
import {ArcCreationStepper} from '../../arc-creation-stepper/arc-creation-stepper';
import {DateSelect} from '../../../shared/date-select/date-select';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {InputField} from '../../../shared/input-field/input-field';

@Component({
    selector: 'app-arc-details-step',
    imports: [
        CardPageLayout,
        ArcCreationStepper,
        DateSelect,
        MatIcon,
        PrimaryButton,
        InputField,
    ],
    templateUrl: './arc-details-step.html',
    styleUrl: './arc-details-step.css',
})
export class ArcDetailsStep {
    today = new Date();
    startDate = signal<Date | null>(null);
    endDate = signal<Date | null>(null);
    arcName = signal('');
    submitted = signal(false);

    nextStep = output();

    constructor() {
        effect(() => {
            const start = this.startDate();
            const end = untracked(() => this.endDate());
            if (start && end && start >= end) {
                this.endDate.set(null);
            }
        });
    }

    arcNameError = computed(() =>
        this.submitted() && !this.arcName().trim() ? 'Name your arc' : null
    );

    endDateError = computed(() =>
        this.submitted() && !this.endDate() ? 'Pick an end date' : null
    );

    goToNextStep() {
        this.submitted.set(true);
        if (!this.arcName().trim() || !this.endDate()) return;
        this.nextStep.emit();
    }
}