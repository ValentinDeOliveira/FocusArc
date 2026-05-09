import {Component, Input, signal} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';

@Component({
    selector: 'app-password-field',
    imports: [ReactiveFormsModule],
    templateUrl: './password-field.html',
    styleUrls: ['../input-field/input-field.css', './password-field.css', '../form-shared.css' ],
})
export class PasswordField {
    @Input({ required: true }) control!: FormControl;
    @Input() inputId = 'password';
    @Input() placeholder = 'Your password';
    @Input() autocomplete: 'current-password' | 'new-password' = 'current-password';

    showPassword = signal(false);

    togglePassword(): void {
        this.showPassword.update(v => !v);
    }
}
