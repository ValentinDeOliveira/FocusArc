import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {

    render(element: HTMLElement, onCredential: (token: string) => void): void {
        const setup = () => {
            google.accounts.id.initialize({
                client_id: environment.googleClientId,
                callback: r => onCredential(r.credential),
            });
            google.accounts.id.renderButton(element, { type: 'standard', theme: 'outline', size: 'large' });
        };
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        typeof google !== 'undefined' ? setup() : (window as any).onGoogleLibraryLoad = setup;
    }
}