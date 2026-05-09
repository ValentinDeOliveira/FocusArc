import {Component, Input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';

@Component({
    selector: 'app-form-input-field',
    imports: [ReactiveFormsModule],
    templateUrl: './form-input-field.html',
    styleUrl: './input-field.css',
})
export class FormInputField {
    @Input({ required: true }) control!: FormControl;
    @Input({ required: true }) label!: string;
    @Input() inputId = '';
    @Input() type = 'text';
    @Input() placeholder = '';
    @Input() autocomplete = '';
    @Input() errorMessage = '';

    get effectiveId(): string {
        return this.inputId || this.label.toLowerCase().replace(/\s+/g, '-');
    }
}