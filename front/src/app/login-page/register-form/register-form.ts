import {Component, inject, signal} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../core/services/auth.service';

@Component({
    selector: 'app-register-form',
    imports: [ReactiveFormsModule],
    templateUrl: './register-form.html',
    styleUrl: '../form-shared.css',
})
export class RegisterForm {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    showPassword = signal(false);
    loading = signal(false);
    error = signal<string | null>(null);

    form = this.fb.group({
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
    });

    togglePassword(): void {
        this.showPassword.update(v => !v);
    }

    onSubmit(): void {
        if (this.form.invalid) return;
        this.loading.set(true);
        this.error.set(null);
        const { name, email, password } = this.form.value;
        this.authService.register({ name: name!, email: email!, password: password! }).subscribe({
            next: () => this.router.navigate(['/']),
            error: () => {
                this.error.set('Registration failed. Please try again.');
                this.loading.set(false);
            },
        });
    }
}
