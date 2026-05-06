export function enumToCssClass(value: string): string {
    return value.toLowerCase().replace(/_/g, '-');
}

export function toKebabCase(value: string): string {
    return value.toLowerCase().replace(/\s+/g, '-');
}