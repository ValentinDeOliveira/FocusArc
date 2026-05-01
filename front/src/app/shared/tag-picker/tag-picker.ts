import {Component, EventEmitter, HostListener, inject, Input, Output} from '@angular/core';
import {Tag} from '../../models/tag.model';
import {TagStore} from '../../core/stores/tag.store';
import {TagDot} from '../tag-dot/tag-dot';

@Component({
    selector: 'app-tag-picker',
    imports: [TagDot],
    templateUrl: './tag-picker.html',
    styleUrl: './tag-picker.css',
})
export class TagPicker {
    @Input() selectedTag: Tag | null = null;
    @Output() tagChange = new EventEmitter<Tag | null>();

    protected isOpen = false;
    protected tagStore = inject(TagStore);

    protected select(tag: Tag | null = null) {
        this.tagChange.emit(tag);
        this.isOpen = false;
    }

    @HostListener('document:click', ['$event'])
    protected onDocumentClick(event: MouseEvent) {
        if (!(event.target as HTMLElement).closest('app-tag-picker')) {
            this.isOpen = false;
        }
    }
}
