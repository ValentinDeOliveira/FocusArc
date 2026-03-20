import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ArcProgress} from './features/arc-section/arc-progress/arc-progress';
import {DashboardResume} from './features/dashboard/dashboard-resume/dashboard-resume';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet,  ArcProgress, DashboardResume],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('front');
}
