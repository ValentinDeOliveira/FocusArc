import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Dashboard} from './features/dashboard/dashboard';
import {ArcCard} from './features/arc-section/arc-card/arc-card';
import {ArcProgress} from './features/arc-section/arc-progress/arc-progress';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, Dashboard, ArcProgress],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('front');
}
