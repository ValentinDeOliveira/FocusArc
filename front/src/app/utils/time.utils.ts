export function padTime(val: number): string {
    return val.toString().padStart(2, '0');
}

export function parseTime(time: string): [number, number] {
    const [h, m] = time.split(':').map(Number);
    return [h, m];
}

export function formatMinutes(totalMinutes: number): string {
    if (totalMinutes === 0) return "";

    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;

    if (hours === 0) return `${minutes}m`;
    if (minutes === 0) return `${hours}h`;

    return `${hours}h ${minutes}m`;
}

export function formatSeconds(s: number): string {
    const m = Math.floor(s / 60);
    const sec = s % 60;
    return `${padTime(m)}:${padTime(sec)}`;
}
