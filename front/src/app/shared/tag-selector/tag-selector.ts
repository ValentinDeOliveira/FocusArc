import {Component, HostListener, inject, input, OnInit, output, signal} from '@angular/core';
import {Tag} from '../../models/tag.model';
import {TAG_COLORS, TAG_COLORS_KEYS, TagColor} from '../../models/tag-colors';
import {TagService} from '../../core/services/tag.service';
import {TagPill} from '../tag-pill/tag-pill';
import {MatIcon} from '@angular/material/icon';
import {CapitalizePipe} from '../pipes/capitalize.pipe';
import {CharCounter} from '../char-counter/char-counter';
import {FIELD_LIMITS} from '../field-limits';

@Component({
    selector: 'app-tag-selector',
    imports: [TagPill, MatIcon, CapitalizePipe, CharCounter],
    templateUrl: './tag-selector.html',
    styleUrl: './tag-selector.css',
    host: { '(click)': '$event.stopPropagation()' }
})
export class TagSelector implements OnInit {
    selectedTag = input<Tag | null>(null);
    tagChange = output<Tag | null>();

    protected newTagName = signal('');
    protected pendingColor = signal<TagColor>('RED');
    protected isOpen = signal(false);

    protected readonly TAG_MAX_LENGTH = FIELD_LIMITS.TAG_NAME;

    protected readonly TAG_COLORS = TAG_COLORS;
    protected readonly TAG_COLORS_KEYS = TAG_COLORS_KEYS;

    private tags = signal<Tag[]>([]);
    protected availableTags = this.tags.asReadonly();

    private tagService = inject(TagService);

    ngOnInit() {
        this.tagService.getAllForUser().subscribe(tags => this.tags.set(tags));
    }

    @HostListener('document:click')
    protected closeDropdown() {
        if (this.isOpen()) {
            this.commitPending();
        }
        this.isOpen.set(false);
    }

    protected openDropdown() {
        if (!this.isOpen()) {
            this.pendingColor.set(this.selectedTag()?.color ?? 'RED');
        }
        this.isOpen.set(true);
    }

    protected toggleDropdown(event: Event) {
        event.stopPropagation();
        if (this.isOpen()) {
            this.commitPending();
            this.isOpen.set(false);
        } else {
            this.pendingColor.set(this.selectedTag()?.color ?? 'RED');
            this.isOpen.set(true);
        }
    }

    protected removeTag(event: Event): void {
        event.stopPropagation();
        this.emitAndResetTag();
    }

    protected selectTag(tag: Tag) {
        this.emitAndResetTag(tag);
    }

    protected selectColor(color: TagColor) {
        this.pendingColor.set(color);
    }

    protected createTagOnEnter(event: Event) {
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
}
