import {Component, computed, input, output, signal} from '@angular/core';
import {toKebabCase} from '../../utils/string.utils';
import {CharCounter} from '../char-counter/char-counter';

const DEFAULT_MAX_LENGTH = 50;

@Component({
    selector: 'app-input-field',
    imports: [CharCounter],
    templateUrl: './input-field.html',
    styleUrl: './input-field.css',
})
export class InputField {
    maxLength = input<number>(DEFAULT_MAX_LENGTH);

    label = input.required<string>();
    id = input<string>('');
    placeholder = input('');
    type = input('text');
    error = input<string | null>(null);
    value = input<string>('');
    valueChange = output<string>();

    private computedId = computed(() => toKebabCase(this.label()));
    effectiveId = computed(() => this.id() || this.computedId());

    protected currentLength = signal(0);

    onInput(val: string) {
        this.valueChange.emit(val);
        this.currentLength.set(val.length);
    }
}