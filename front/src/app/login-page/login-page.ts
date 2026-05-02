import { Component, signal } from '@angular/core';
import { LoginForm } from './login-form/login-form';
import { RegisterForm } from './register-form/register-form';
import { CardPageLayout } from '../shared/card-page-layout/card-page-layout';

type Tab = 'login' | 'register';

@Component({
    selector: 'app-login-page',
    imports: [LoginForm, RegisterForm, CardPageLayout],
    templateUrl: './login-page.html',
    styleUrl: './login-page.css',
})
export class LoginPage {
    activeTab = signal<Tab>('login');

    setTab(tab: Tab): void {
        this.activeTab.set(tab);
    }
}