import {TagColor} from './tag.model';

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

export const TAG_COLORS_KEYS = Object.keys(TAG_COLORS) as TagColor[];

const DEFAULT_EVENT_COLOR = '#3b5bdb';
const DEFAULT_NEUTRAL_COLOR = '#6b7280';

export function color(c: TagColor): string {
    return TAG_COLORS[c];
}

/** Background tint — 10% opacity, used for pill/chip backgrounds. */
export function colorBg(c: TagColor): string {
    return TAG_COLORS[c] + '1a';
}

/** Hex fallback for charts/stats — returns a neutral gray when there is no tag. */
export function colorOrNeutral(c?: TagColor | null): string {
    return c ? TAG_COLORS[c] : DEFAULT_NEUTRAL_COLOR;
}

/** Calendar event color object — primary border/text + 20% tint background.
 *  Uses a hex fallback (not a CSS variable) so the alpha suffix stays valid. */
export function eventColor(c?: TagColor | null): { primary: string; secondary: string; secondaryText: string } {
    const primary = c ? TAG_COLORS[c] : DEFAULT_EVENT_COLOR;
    return { primary, secondary: primary + '33', secondaryText: primary };
}
