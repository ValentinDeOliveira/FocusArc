import {AfterViewInit, Directive, ElementRef, inject, signal, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthService} from '../core/services/auth.service';
import {GoogleAuthService} from '../core/services/google-auth.service';

@Directive()
export abstract class AuthFormBase implements AfterViewInit {
    protected authService = inject(AuthService);
    protected router = inject(Router);
    private googleAuthService = inject(GoogleAuthService);

    @ViewChild('googleBtnContainer') private googleBtnContainer?: ElementRef<HTMLDivElement>;

    loading = signal(false);
    error = signal<string | null>(null);

    ngAfterViewInit(): void {
        if (this.googleBtnContainer) {
            this.googleAuthService.render(
                this.googleBtnContainer.nativeElement,
                token => this.handleGoogleCredential(token),
            );
        }
    }

    private handleGoogleCredential(idToken: string): void {
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

    protected abstract onGoogleSuccess(): void;
    protected abstract readonly googleErrorMessage: string;
    abstract getErrorMessage(error: HttpErrorResponse): string;
}