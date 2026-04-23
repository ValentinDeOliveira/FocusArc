import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../core/services/auth.service';
import {PasswordField} from '../../shared/password-field/password-field';
import {HttpErrorResponse} from '@angular/common/http';
import {ApiErrorType} from '../../models/api-error.model';

@Component({
    selector: 'app-register-form',
    imports: [ReactiveFormsModule, PasswordField],
    templateUrl: './register-form.html',
    styleUrl: '../form-shared.css',
})
export class RegisterForm {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    loading = signal(false);
    error = signal<string | null>(null);

    form = this.fb.group({
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
    });

    onSubmit(): void {
        this.form.markAllAsTouched();
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { name, email, password } = this.form.value;
        this.authService.register({ name: name!, email: email!, password: password! }).subscribe({
            next: () => this.router.navigate(['/']),
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
