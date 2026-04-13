import { Routes } from '@angular/router';
import {Home} from './features/home/home';
import {ArcViewPage} from './features/arc-view-page/arc-view-page';
import {arcResolver} from './features/arc-view-page/arc.resolver';

export const routes: Routes = [
    { path: '', component: Home },
    { path: 'arc', component: ArcViewPage, resolve: {chapters: arcResolver} },
];
