export enum Provider {
    Google = 'GOOGLE',
    Local = 'LOCAL',
}

export enum ApiErrorType {
    NoChapterForArcException = 'NoChapterForArcException',
    EmailAlreadyExistsException = 'EmailAlreadyExistsException',
    InvalidCredentialsException = 'InvalidCredentialsException',
    AccountAlreadyExistsWithProviderException = 'AccountAlreadyExistsWithProviderException'
}
