import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {ArcViewPage} from './features/arc-view-page/arc-view-page';
import {arcResolver} from './features/arc-view-page/arc.resolver';
import {LoginPage} from './login-page/login-page';
import {ArcCreation} from './arc-creation-workflow/arc-creation/arc-creation';

export const routes: Routes = [
    { path: 'login', component: LoginPage },
    { path: '', component: Home },
    { path: 'arc', component: ArcViewPage, resolve: {chapters: arcResolver} },
    { path: 'arc-creation', component: ArcCreation },
];
