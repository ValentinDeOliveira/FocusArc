import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../core/services/auth.service';
import {PasswordField} from '../password-field/password-field';
import {HttpErrorResponse} from '@angular/common/http';
import {ApiErrorType} from '../../models/api-error.model';

@Component({
    selector: 'app-login-form',
    imports: [ReactiveFormsModule, PasswordField],
    templateUrl: './login-form.html',
    styleUrl: './login-form.css',
})
export class LoginForm {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    loading = signal(false);
    error = signal<string | null>(null);

    form = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        rememberMe: [false],
    });

    onSubmit(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { email, password } = this.form.value;
        this.authService.login({ email: email!, password: password! }).subscribe({
            next: () => this.router.navigate(['/']),
            error: (error: HttpErrorResponse) => {
                this.error.set(this.getErrorMessage(error));
                this.loading.set(false);
            },
        });
    }

    getErrorMessage(error: HttpErrorResponse): string {
        if (error.error.error == ApiErrorType.InvalidCredentialsException) {
            return 'Invalid email or password.';
        }

        return 'Login failed. Please try again.';
    }
}
