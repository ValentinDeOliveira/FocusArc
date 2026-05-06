import {Component, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {PasswordField} from '../../shared/password-field/password-field';
import {ApiErrorType, Provider} from '../../models/api-error.model';
import {AuthFormBase} from '../auth-form-base';

@Component({
    selector: 'app-login-form',
    imports: [ReactiveFormsModule, PasswordField],
    templateUrl: './login-form.html',
    styleUrl: './login-form.css',
})
export class LoginForm extends AuthFormBase {
    private fb = inject(FormBuilder);

    protected override readonly googleErrorMessage = 'Google sign-in failed. Please try again.';

    form = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        rememberMe: [false],
    });

    protected override onGoogleSuccess(): void {
        void this.router.navigate(['/']);
    }

    onSubmit(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { email, password } = this.form.value;
        this.authService.login({ email: email!, password: password! }).subscribe({
            next: () => void this.router.navigate(['/']),
            error: (error: HttpErrorResponse) => {
                this.error.set(this.getErrorMessage(error));
                this.loading.set(false);
            },
        });
    }

    getErrorMessage(error: HttpErrorResponse): string {
        if (error.error.error === ApiErrorType.InvalidCredentialsException) {
            return 'Invalid email or password.';
        }
        if (error.error.error === ApiErrorType.AccountAlreadyExistsWithProviderException) {
            if (error.error.details.provider === Provider.Google) {
                return 'This email is linked to a Google account. Sign in with Google instead.';
            }
            if (error.error.details.provider === Provider.Local) {
                return 'This email is linked to an account. Sign in with email and password instead.';
            }
        }
        return 'Login failed. Please try again.';
    }
}