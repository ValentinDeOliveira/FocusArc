import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
    private clientId = environment.googleClientId;
    private callback: ((credential: string) => void) | null = null;
    private initialized = false;

    register(callback: (credential: string) => void): void {
        this.callback = callback;
        if (!this.initialized) {
            this.initialized = true;
            this.whenGoogleReady(() =>
                google.accounts.id.initialize({
                    client_id: this.clientId,
                    callback: ({ credential }) => this.callback?.(credential),
                })
            );
        }
    }

    prompt(): void {
        if (typeof google !== 'undefined') {
            google.accounts.id.prompt();
        }
    }

    private whenGoogleReady(fn: () => void): void {
        if (typeof google !== 'undefined') {
            fn();
        } else {
            (window as Window & { onGoogleLibraryLoad?: () => void }).onGoogleLibraryLoad = fn;
        }
    }
}