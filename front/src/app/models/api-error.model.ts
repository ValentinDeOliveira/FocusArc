export enum ApiErrorType {
    NoChapterForArcException = 'NoChapterForArcException',
}

export interface ApiError {
    status: number;
    error: ApiErrorType;
    message: string;
    timestamp: string;
}