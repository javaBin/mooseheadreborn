export enum WorkshopStatus {
    OPEN = "OPEN",
    NOT_OPEN = "NOT_OPEN",
    FULL = "FULL",
    CLOSED = "CLOSED"
}

export interface WorkshopType {
    id: string;
    name: string;
    workshopstatus: WorkshopStatus;
    opensAt: string;
    registerLimit: number;
}

export interface AddParticipantInput {
    name: String,
    email: String
}

export enum RegistrationStatus {
    NOT_LOGGED_IN = "NOT_LOGGED_IN",
    NOT_REGISTERED = "NOT_REGISTERED",
    REGISTERED = "REGISTERED",
    WAITING = "WAITING",
    CANCELLED = "CANCELLED",
}

export interface WorkshopInfoFromServer {
    workshop: WorkshopType;
    registrationStatus: RegistrationStatus;
    registrationStatusText:string;
    registrationId:string|null;
    numRegistered:number|null;
}

export interface AddRegistrationInput {
    accessToken:string;
    workshopId:string,
    numParticipants:number
}

export interface AddRegistrationOutput {
    registrationStatus:RegistrationStatus;
    registrationId:string;
}

export interface CancelRegistrationInput {
    registrationId: string;
    accessToken: string|null;
}

export interface CancelRegistrationOutput {
    registrationStatus:RegistrationStatus;
}

export enum UserType {
    USER = "USER",
    ADMIN = "ADMIN",
    ANONYMOUS = "ANONYMOUS",
}

export interface UserLogin {
    accessToken: string|null;
    name: string|null,
    email: string|null,
    userType:UserType
}

export const defaultUserLogin:UserLogin = {
    accessToken: null,
    name: null,
    email: null,
    userType:UserType.ANONYMOUS
}