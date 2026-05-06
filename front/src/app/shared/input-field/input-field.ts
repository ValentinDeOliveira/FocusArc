import {Component, computed, input, output} from '@angular/core';
import {toKebabCase} from '../../utils/string.utils';

@Component({
    selector: 'app-input-field',
    imports: [],
    templateUrl: './input-field.html',
    styleUrl: './input-field.css',
})
export class InputField {
    label = input.required<string>();
    id = input<string>('');
    placeholder = input('');
    type = input('text');
    error = input<string | null>(null);
    value = input<string>('');
    valueChange = output<string>();

    private computedId = computed(() => toKebabCase(this.label()));
    effectiveId = computed(() => this.id() || this.computedId());
}
