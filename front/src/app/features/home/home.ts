import {Component} from '@angular/core';
import {ArcProgress} from '../arc-section/arc-progress/arc-progress';
import {DashboardResume} from '../dashboard/dashboard-resume/dashboard-resume';

@Component({
    selector: 'app-home',
    imports: [ArcProgress, DashboardResume],
    templateUrl: './home.html',
    styleUrl: './home.css',
})
export class Home {}