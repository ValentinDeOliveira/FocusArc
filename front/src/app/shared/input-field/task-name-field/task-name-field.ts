import {Component, computed, output, signal} from '@angular/core';
import {InputField} from '../input-field';
import {FIELD_LIMITS} from '../../field-limits';

@Component({
    selector: 'app-task-name-field',
    imports: [InputField],
    templateUrl: './task-name-field.html',
    styleUrl: './task-name-field.css',
})
export class TaskNameField {
    valueChange = output<string>();

    protected name = signal('');
    private submitted = signal(false);
    protected taskNameError = computed(() =>
        this.submitted() && !this.name().trim() ? 'Name your task' : null
    );
    protected readonly FIELD_LIMITS = FIELD_LIMITS;

    protected onChange(value: string) {
        this.name.set(value);
        this.valueChange.emit(value);
    }

    validate(): boolean {
        this.submitted.set(true);
        return !!this.name().trim();
    }

    reset(): void {
        this.name.set('');
        this.submitted.set(false);
    }

    getValue(): string {
        return this.name();
    }
}