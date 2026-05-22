import {Component, Input} from '@angular/core';
import {MatCard} from '@angular/material/card';

export interface StatRow {
    label: string;
    color: string;
    done: number;
    total: number;
    pct: number;
}

@Component({
    selector: 'app-arc-stats-base',
    imports: [MatCard],
    templateUrl: './arc-stats-base.html',
    styleUrl: './arc-stats-base.css',
})
export class ArcStatsBase {
    @Input({ required: true }) cardLabel!: string;
    @Input({ required: true }) rows!: StatRow[];
    @Input() hasContent: boolean = false;

    get shouldRender(): boolean {
        return this.rows.length > 0 || this.hasContent;
    }
}
