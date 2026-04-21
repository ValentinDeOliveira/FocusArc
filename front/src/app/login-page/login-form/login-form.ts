import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
    selector: 'app-login-form',
    imports: [ReactiveFormsModule],
    templateUrl: './login-form.html',
    styleUrl: './login-form.css',
})
export class LoginForm {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    showPassword = signal(false);
    loading = signal(false);
    error = signal<string | null>(null);

    form = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        rememberMe: [false],
    });

    togglePassword(): void {
        this.showPassword.update(v => !v);
    }

    onSubmit(): void {
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { email, password } = this.form.value;
        this.authService.login({ email: email!, password: password! }).subscribe({
            next: () => this.router.navigate(['/']),
            error: () => {
                this.error.set('Invalid email or password.');
                this.loading.set(false);
            },
        });
    }
}