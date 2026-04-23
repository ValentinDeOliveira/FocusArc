export enum ApiErrorType {
    NoChapterForArcException = 'NoChapterForArcException',
    EmailAlreadyExistsException = 'EmailAlreadyExistsException',
    InvalidCredentialsException = 'InvalidCredentialsException'
}

export interface ApiError {
    status: number;
    error: ApiErrorType;
    message: string;
    timestamp: string;
}
