import {Component, computed, input} from '@angular/core';

@Component({
    selector: 'app-char-counter',
    imports: [],
    templateUrl: './char-counter.html',
    styleUrl: './char-counter.css',
})
export class CharCounter {
    current = input.required<number>();
    max = input.required<number>();

    protected visible = computed(() => this.current() >= this.max() * 0.8);
    protected label = computed(() => `${this.current()}/${this.max()}`);
}