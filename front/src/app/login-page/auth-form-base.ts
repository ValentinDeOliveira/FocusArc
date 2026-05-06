import {Directive, inject, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthService} from '../core/services/auth.service';
import {GoogleAuthService} from '../core/services/google-auth.service';

@Directive()
export abstract class AuthFormBase implements OnInit {
    protected authService = inject(AuthService);
    protected router = inject(Router);
    private googleAuthService = inject(GoogleAuthService);

    loading = signal(false);
    error = signal<string | null>(null);

    ngOnInit(): void {
        this.googleAuthService.register((credential) => this.handleGoogleCredential(credential));
    }

    handleGoogleCredential(idToken: string): void {
        this.loading.set(true);
        this.error.set(null);
        this.authService.loginWithGoogle({ idToken }).subscribe({
            next: () => this.onGoogleSuccess(),
            error: () => {
                this.error.set(this.googleErrorMessage);
                this.loading.set(false);
            },
        });
    }

    promptGoogle(): void {
        this.googleAuthService.prompt();
    }

    protected abstract onGoogleSuccess(): void;
    protected abstract readonly googleErrorMessage: string;
    abstract getErrorMessage(error: HttpErrorResponse): string;
}