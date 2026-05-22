import {Component, computed, input, model} from '@angular/core';
import {toKebabCase} from '../../utils/string.utils';

@Component({
    selector: 'app-number-field',
    imports: [],
    templateUrl: './number-field.html',
    styleUrls: ['../input-field/input-field.css', './number-field.css'],
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

    protected onValueChange(raw: number): void {
        const s = this.step();
        const mn = this.min() ?? 0;
        const mx = this.max() ?? 1000;
        const snapped = Math.round((raw - mn) / s) * s + mn;
        this.value.set(Math.min(mx, Math.max(mn, snapped)));
    }
}
