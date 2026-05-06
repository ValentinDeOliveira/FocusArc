import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {PasswordField} from '../../shared/password-field/password-field';
import {ApiErrorType} from '../../models/api-error.model';
import {AuthFormBase} from '../auth-form-base';

@Component({
    selector: 'app-register-form',
    imports: [ReactiveFormsModule, PasswordField],
    templateUrl: './register-form.html',
    styleUrl: '../../shared/form-shared.css',
})
export class RegisterForm extends AuthFormBase {
    private fb = inject(FormBuilder);

    protected override readonly googleErrorMessage = 'Google sign-up failed. Please try again.';

    form = this.fb.group({
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
    });

    protected override onGoogleSuccess(): void {
        void this.router.navigate(['/arc-creation']);
    }

    onSubmit(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { name, email, password } = this.form.value;
        this.authService.register({ name: name!, email: email!, password: password! }).subscribe({
            next: () => void this.router.navigate(['/arc-creation']),
            error: (error: HttpErrorResponse) => {
                this.error.set(this.getErrorMessage(error));
                this.loading.set(false);
            },
        });
    }

    getErrorMessage(error: HttpErrorResponse): string {
        if (error.error.error == ApiErrorType.EmailAlreadyExistsException) {
            return 'An account already exists with this email, please try to login';
        }
        return 'Registration failed. Please try again.';
    }
}