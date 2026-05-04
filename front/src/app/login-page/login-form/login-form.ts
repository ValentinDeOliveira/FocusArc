import {Component, inject, OnInit, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthService} from '../../core/services/auth.service';
import {PasswordField} from '../../shared/password-field/password-field';
import {ApiErrorType, Provider} from '../../models/api-error.model';

@Component({
    selector: 'app-login-form',
    imports: [ReactiveFormsModule, PasswordField],
    templateUrl: './login-form.html',
    styleUrl: './login-form.css',
})
export class LoginForm implements OnInit {
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

    ngOnInit(): void {
        google.accounts.id.initialize({
            client_id: '468172227016-rb385r55f5hlm995o0mnfg2ldj7s85jl.apps.googleusercontent.com',
            callback: ({ credential }) => this.handleGoogleCredential(credential),
        });
    }

    handleGoogleCredential(idToken: string): void {
        this.loading.set(true);
        this.error.set(null);
        this.authService.loginWithGoogle({ idToken }).subscribe({
            next: () => void this.router.navigate(['/']),
            error: () => {
                this.error.set('Google sign-in failed. Please try again.');
                this.loading.set(false);
            },
        });
    }

    loginWithGoogle(): void {
        google.accounts.id.prompt();
    }

    onSubmit(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { email, password } = this.form.value;
        this.authService.login({ email: email!, password: password! })
            .subscribe({
                next: () => void this.router.navigate(['/']),
                error: (error: HttpErrorResponse) => {
                    this.error.set(this.getErrorMessage(error));
                    this.loading.set(false);
                }
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
