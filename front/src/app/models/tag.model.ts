export type TagColor = 'RED' | 'ORANGE' | 'YELLOW' | 'GREEN' | 'TEAL' | 'BLUE' | 'PURPLE' | 'PINK';
export const TAG_COLORS: Record<TagColor, string> = {
    RED: '#ef4444',
    ORANGE: '#f59e0b',
    YELLOW: '#eab308',
    GREEN: '#10b981',
    TEAL: '#14b8a6',
    BLUE: '#6366f1',
    PURPLE: '#a855f7',
    PINK: '#ec4899'
};

export interface Tag {
    id: string;
    ownerId: string;
    label: string;
    color: TagColor;
}

export interface TagCreationDto {
    label: string;
    color: TagColor;
}

export interface TagUpdateDto {
    label: string;
    color: TagColor;
}

export interface TagStatDto {
    tagId: string;
    total: number;
    done: number;
}

export function color(tag?: Tag | null): string {
    return tag ? TAG_COLORS[tag.color] : 'var(--color-border)';
}
