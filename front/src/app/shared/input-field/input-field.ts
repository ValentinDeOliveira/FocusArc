import {Component, computed, input, output} from '@angular/core';

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
    valueChange = output<string>();

    private computedId = computed(() => this.label().toLowerCase().replace(/\s+/g, '-'));
    effectiveId = computed(() => this.id() || this.computedId());
}
