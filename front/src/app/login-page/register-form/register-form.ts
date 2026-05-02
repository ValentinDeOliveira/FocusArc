import {Component, inject, NgZone, OnInit, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../core/services/auth.service';
import {PasswordField} from '../../shared/password-field/password-field';
import {HttpErrorResponse} from '@angular/common/http';
import {ApiErrorType} from '../../models/api-error.model';
import {AuthResponseDto} from '../../models/auth.model';

@Component({
    selector: 'app-register-form',
    imports: [ReactiveFormsModule, PasswordField],
    templateUrl: './register-form.html',
    styleUrl: '../../shared/form-shared.css',
})
export class RegisterForm implements OnInit {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);
    private ngZone = inject(NgZone);

    loading = signal(false);
    error = signal<string | null>(null);

    form = this.fb.group({
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
    });

    ngOnInit(): void {
        google.accounts.id.initialize({
            client_id: '468172227016-rb385r55f5hlm995o0mnfg2ldj7s85jl.apps.googleusercontent.com',
            callback: ({ credential }) => this.ngZone.run(() => this.handleGoogleCredential(credential)),
        });
    }

    registerWithGoogle(): void {
        google.accounts.id.prompt();
    }

    handleGoogleCredential(idToken: string): void {
        this.loading.set(true);
        this.error.set(null);
        this.authService.loginWithGoogle({ idToken }).subscribe({
            next: (res) => this.continueToArcCreation(res),
            error: () => {
                this.error.set('Google sign-up failed. Please try again.');
                this.loading.set(false);
            },
        });
    }

    onSubmit(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { name, email, password } = this.form.value;
        this.authService.register({ name: name!, email: email!, password: password! }).subscribe({
            next: (res) => this.continueToArcCreation(res),
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

    private continueToArcCreation(authResponse: AuthResponseDto) {
        localStorage.setItem('token', authResponse.accessToken);
        localStorage.setItem('refreshToken', authResponse.refreshToken);
        void this.router.navigate(['/arc-creation']);
    }
}
