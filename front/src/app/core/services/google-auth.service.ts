import {Injectable} from '@angular/core';

const GOOGLE_CLIENT_ID = '468172227016-rb385r55f5hlm995o0mnfg2ldj7s85jl.apps.googleusercontent.com';

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
    private callback: ((credential: string) => void) | null = null;
    private initialized = false;

    register(callback: (credential: string) => void): void {
        this.callback = callback;
        if (!this.initialized) {
            this.initialized = true;
            google.accounts.id.initialize({
                client_id: GOOGLE_CLIENT_ID,
                callback: ({ credential }) => this.callback?.(credential),
            });
        }
    }

    prompt(): void {
        google.accounts.id.prompt();
    }
}