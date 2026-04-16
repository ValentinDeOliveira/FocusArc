export type TagColor = 'RED' | 'ORANGE' | 'YELLOW' | 'GREEN' | 'TEAL' | 'BLUE' | 'PURPLE' | 'PINK' | 'GRAY';

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
