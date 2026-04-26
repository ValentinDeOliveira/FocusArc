import {booleanAttribute, Component, input} from '@angular/core';

@Component({
    selector: 'app-card-page-layout',
    templateUrl: './card-page-layout.html',
    styleUrl: './card-page-layout.css',
})
export class CardPageLayout {
    title = input.required<string>();
    subtitle = input.required<string>();
    wide = input(false, { transform: booleanAttribute });
}
