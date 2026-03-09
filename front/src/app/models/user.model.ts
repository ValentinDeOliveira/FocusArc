export interface User {
    id: string;
    name: string;
    email: string;
    password: string;
    lastLogin: string;
}

export interface UserUpdateDto {
    name: string;
}
