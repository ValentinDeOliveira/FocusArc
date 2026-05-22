import {booleanAttribute, Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';

@Component({
    selector: 'app-primary-btn',
    imports: [RouterLink],
    templateUrl: './primary-button.html',
    styleUrl: './primary-button.css',
    host: {
        '[style.display]': 'fullWidth ? "block" : null',
        '[style.width]': 'fullWidth ? "100%" : null',
    },
})
export class PrimaryButton {
    @Input() fullWidth = false;
    @Input() routerLink?: string | any[];
    @Input() disabled = false;
    @Input({ transform: booleanAttribute }) ghost = false;
    @Input({ transform: booleanAttribute }) light = false;
}
