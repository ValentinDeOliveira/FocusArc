import {Component, computed, input, model} from '@angular/core';

@Component({
    selector: 'app-number-field',
    imports: [],
    templateUrl: './number-field.html',
    styleUrls: ['../form-shared.css', './number-field.css'],
})
export class NumberField {
    label = input<string>('');
    id = input<string>('');

    placeholder = input('');
    min = input<number | null>(null);
    max = input<number | null>(null);
    step = input<number>(1);
    defaultValue = input<number>(1);
    value = model<number | null>(null);

    private computedId = computed(() => this.label().toLowerCase().replace(/\s+/g, '-') || 'number-field');
    effectiveId = computed(() => this.id() || this.computedId());
    protected readonly HTMLInputElement = HTMLInputElement;
}
