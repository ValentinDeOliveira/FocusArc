import {Component, Input} from '@angular/core';
import {Tag, TagColor} from '../../models/tag.model';

export const TAG_COLORS: Record<TagColor, string> = {
    RED: '#ef4444',
    ORANGE: '#f59e0b',
    YELLOW: '#eab308',
    GREEN: '#10b981',
    TEAL: '#14b8a6',
    BLUE: '#6366f1',
    PURPLE: '#a855f7',
    PINK: '#ec4899',
    GRAY: '#6b7280',
};

@Component({
    selector: 'app-tag-dot',
    templateUrl: './tag-dot.html',
    styleUrl: './tag-dot.css',
})
export class TagDot {
    @Input() tag: Tag | null = null;

    get color(): string {
        return this.tag ? TAG_COLORS[this.tag.color] : 'var(--color-border)';
    }
}
