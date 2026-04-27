import {Component, inject, signal} from '@angular/core';
import {FormBuilder, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {CardPageLayout} from '../../shared/card-page-layout/card-page-layout';
import {ArcCreationStepper} from '../arc-creation-stepper/arc-creation-stepper';
import {DateSelect} from '../../shared/date-select/date-select';
import {MatIcon} from '@angular/material/icon';
import {PrimaryButton} from '../../shared/primary-button/primary-button';

@Component({
    selector: 'app-arc-creation',
    imports: [
        FormsModule,
        ReactiveFormsModule,
        CardPageLayout,
        ArcCreationStepper,
        DateSelect,
        MatIcon,
        PrimaryButton,
    ],
    templateUrl: './arc-creation.html',
    styleUrls: [
        './arc-creation.css',
        '../../shared/form-shared.css'
    ]
})
export class ArcCreation {
    private fb = inject(FormBuilder);

    today = new Date();
    startDate = signal<Date | null>(null);

    form = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        rememberMe: [false],
    });

}
