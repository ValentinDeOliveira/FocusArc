import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {ArcViewPage} from './features/arc-view-page/arc-view-page';
import {arcResolver} from './features/arc-view-page/arc.resolver';
import {LoginPage} from './login-page/login-page';
import {ArcCreation} from './arc-creation-workflow/arc-creation/arc-creation';
import {hasArcGuard} from './core/guards/has-arc.guard';
import {ArcCompletion} from './features/arc-completion/arc-completion';
import {arcCompletionResolver} from './features/arc-completion/arc-completion.resolver';

export const routes: Routes = [
    { path: 'login', component: LoginPage },
    { path: '', component: Home, canActivate: [hasArcGuard] },
    { path: 'arc', component: ArcViewPage, resolve: {chapters: arcResolver}, canActivate: [hasArcGuard] },
    { path: 'arc-creation', component: ArcCreation },
    { path: 'arc-completion', component: ArcCompletion, resolve: { completion: arcCompletionResolver } },
    { path: '**', redirectTo: '' },
];
