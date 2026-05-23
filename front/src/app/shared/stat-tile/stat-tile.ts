import {Component, input} from '@angular/core';
import {MatIcon} from '@angular/material/icon';

@Component({
    selector: 'app-stat-tile',
    imports: [MatIcon],
    templateUrl: './stat-tile.html',
    styleUrl: './stat-tile.css',
    host: { class: 'stat-tile flex flex-col' },
})
export class StatTile {
    icon = input.required<string>();
    iconClass = input.required<string>();
    label = input.required<string>();
    value = input.required<string | number>();
    /** Renders "/ {{ total }}" after the value */
    total = input<string | number>();
    /** Renders "{{ totalUnit }}" after the value (e.g. "days") */
    totalUnit = input<string>();
    sub = input<string>();
}
