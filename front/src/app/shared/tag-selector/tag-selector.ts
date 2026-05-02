import {Component, HostListener, inject, input, OnInit, output, signal} from '@angular/core';
import {Tag, TAG_COLORS, TagColor} from '../../models/tag.model';
import {TagService} from '../../core/services/tag.service';
import {TagPill} from '../tag-pill/tag-pill';
import {MatIcon} from '@angular/material/icon';

@Component({
    selector: 'app-tag-selector',
    imports: [TagPill, MatIcon],
    templateUrl: './tag-selector.html',
    styleUrl: './tag-selector.css',
    host: { '(click)': '$event.stopPropagation()' }
})
export class TagSelector implements OnInit {
    selectedTag = input<Tag | null>(null);
    tagChange = output<Tag | null>();

    newTagName = signal('');
    pendingColor = signal<TagColor>('BLUE');
    isOpen = signal(false);

    readonly tagColorKeys = Object.keys(TAG_COLORS) as TagColor[];

    private tags = signal<Tag[]>([]);
    availableTags = this.tags.asReadonly();

    private tagService = inject(TagService);

    ngOnInit() {
        this.tagService.getAllForUser().subscribe(tags => this.tags.set(tags));
    }

    @HostListener('document:click')
    closeDropdown() {
        if (this.isOpen()) {
            this.commitPending();
        }
        this.isOpen.set(false);
    }

    openDropdown() {
        if (!this.isOpen()) {
            this.pendingColor.set(this.selectedTag()?.color ?? 'BLUE');
        }
        this.isOpen.set(true);
    }

    toggleDropdown(event: Event) {
        event.stopPropagation();
        if (this.isOpen()) {
            this.commitPending();
            this.isOpen.set(false);
        } else {
            this.pendingColor.set(this.selectedTag()?.color ?? 'BLUE');
            this.isOpen.set(true);
        }
    }

    removeTag(event: Event): void {
        event.stopPropagation();
        this.emitAndResetTag();
    }

    selectTag(tag: Tag) {
        this.emitAndResetTag(tag);
    }

    selectColor(color: TagColor) {
        this.pendingColor.set(color);
    }

    createTagOnEnter(event: Event) {
        event.preventDefault();
        this.commitPending();
        this.isOpen.set(false);
    }

    private commitPending() {
        const tag = this.selectedTag();
        if (tag) {
            // tag has not been updated
            if (this.pendingColor() === tag.color) return;

            this.tagService.update(tag.id, { label: tag.label, color: this.pendingColor() }).subscribe(updated => {
                this.tags.update(list => list.map(t => t.id === updated.id ? updated : t));
                this.tagChange.emit(updated);
            });
        } else {
            const label = this.newTagName().trim();
            if (!label) return;
            this.tagService.create({ label, color: this.pendingColor() }).subscribe(created => {
                this.tags.update(list => [...list, created]);
                this.emitAndResetTag(created);
            });
        }
    }

    private emitAndResetTag(tag: Tag | null = null){
        this.tagChange.emit(tag);
        this.newTagName.set('');
        this.isOpen.set(false);
    }

    protected readonly TAG_COLORS = TAG_COLORS;
}
