import {Component, computed, effect, inject, output, signal, untracked} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {isAfter} from '../../../shared/utils/date-utils';
import {CardPageLayout} from '../../../shared/card-page-layout/card-page-layout';
import {ArcCreationStepper} from '../../arc-creation-stepper/arc-creation-stepper';
import {DateSelect} from '../../../shared/date-select/date-select';
import {PrimaryButton} from '../../../shared/primary-button/primary-button';
import {InputField} from '../../../shared/input-field/input-field';
import {ToastrService} from 'ngx-toastr';
import {ArcService} from '../../../core/services/arc.service';
import {ArcCreationDto} from '../../../models/arc.model';
import {ContextStore} from '../../../core/stores/context.store';

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
export class ArcDetailsStep{
    today = new Date();
    startDate = signal(this.today);
    endDate = signal<Date | null>(null);
    arcName = signal('');
    submitted = signal(false);

    toastr = inject(ToastrService);
    nextStep = output();

    private arcService = inject(ArcService);
    private contextStore = inject(ContextStore);

    constructor() {
        effect(() => {
            const start = this.startDate();
            const end = untracked(() => this.endDate());
            if (end && !isAfter(end, start)) {
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
        // when user modify date manually (without opening calendar)
        if (!isAfter(this.endDate()!, this.startDate())) {
            this.toastr.error("End date is before start date");
            return;
        }

        const dto : ArcCreationDto = {
            name: this.arcName(),
            totalEstimatedMinutes: 0,
            startDate: this.startDate().toISOString(),
            endDate: this.endDate()!.toISOString(),
        }

        this.arcService.create(dto).subscribe(arc => this.contextStore.setArcId(arc.id));
        this.nextStep.emit();
    }
}
