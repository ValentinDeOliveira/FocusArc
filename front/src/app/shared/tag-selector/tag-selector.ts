import {Component, HostListener, inject, input, OnInit, output, signal} from '@angular/core';
import {Observable, of, tap} from 'rxjs';
import {Tag} from '../../models/tag.model';
import {TAG_COLORS, TAG_COLORS_KEYS, TagColor} from '../../models/tag-colors';
import {TagService} from '../../core/services/tag.service';
import {TagStore} from '../../core/stores/tag.store';
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

    private tagStore = inject(TagStore);
    private tagService = inject(TagService);

    protected availableTags = this.tagStore.all;

    ngOnInit() {
        this.tagStore.load().subscribe();
    }

    @HostListener('document:click')
    protected closeDropdown() {
        if (this.isOpen()) {
            this.flushPending().subscribe();
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
            this.flushPending().subscribe();
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
        this.flushPending().subscribe();
        this.isOpen.set(false);
    }

    flushPending(): Observable<Tag | null> {
        this.isOpen.set(false);
        const tag = this.selectedTag();

        if (tag) {
            return this.updateTag(tag);
        }

        const label = this.newTagName().trim();
        if (!label) {
            return of(null);
        }

        const fetchedTag = this.tagStore.byLabel(label);
        if (fetchedTag) {
            return this.updateTag(fetchedTag);
        }

        return this.tagService.create({ label, color: this.pendingColor() }).pipe(
            tap(created => {
                this.tagStore.add(created);
                this.emitAndResetTag(created);
            })
        );
    }

    private updateTag(tag: Tag) {
        // tag has not been updated
        if (this.pendingColor() === tag.color) return of(tag);

        return this.tagService.update(tag.id, { label: tag.label, color: this.pendingColor() }).pipe(
            tap(updated => {
                this.tagStore.upsert(updated);
                this.tagChange.emit(updated);
            })
        );
    }

    private emitAndResetTag(tag: Tag | null = null){
        this.tagChange.emit(tag);
        this.newTagName.set('');
        this.isOpen.set(false);
    }
}
