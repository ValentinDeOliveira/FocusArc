import {Component, computed, input, model} from '@angular/core';
import {toKebabCase} from '../../utils/string.utils';

@Component({
    selector: 'app-number-field',
    imports: [],
    templateUrl: './number-field.html',
    styleUrls: ['./number-field.css', '../input-field/input-field.css'],
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

    private computedId = computed(() => toKebabCase(this.label()) || 'number-field');
    effectiveId = computed(() => this.id() || this.computedId());
    protected readonly HTMLInputElement = HTMLInputElement;
}
