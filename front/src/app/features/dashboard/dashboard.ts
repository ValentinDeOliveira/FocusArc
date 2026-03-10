import {Component} from '@angular/core';
import {DashboardResume} from './dashboard-resume/dashboard-resume';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css',
    imports: [
        DashboardResume
    ]
})
export class Dashboard {
}
