import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-primary-btn',
    templateUrl: './primary-button.html',
    styleUrl: './primary-button.css',
    host: {
        '[style.display]': 'fullWidth ? "block" : null',
    },
})
export class PrimaryButton {
    @Input() fullWidth = false;
}
