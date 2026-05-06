import {TagColor} from './tag-colors';

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

