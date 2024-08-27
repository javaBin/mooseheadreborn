export enum WorkshopStatus {
    OPEN = "OPEN",
    NOT_OPEN = "NOT_OPEN",
    FULL = "FULL",
    CLOSED = "CLOSED"
}

export enum WorkshopServerType {
    JZ = "JZ",
    KIDS = "KIDS"
}

export interface WorkshopType {
    id: string;
    name: string;
    workshopstatus: WorkshopStatus;
    opensAt: string;
    registerLimit: number;
    workshopStatusText:string;
    workshopType:WorkshopServerType;
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

export interface AdminWorkshopRegistrationType {
    id:string;
    status:RegistrationStatus;
    name:string;
    email:string;
    numSpots:number;
    participantId:string;
    registeredAt:string;
}

export interface AdminWorkshopType {
   id: string;
    name: string;
    workshopType:string;
    workshopstatus: WorkshopStatus;
    opensAt: string;
    registerLimit: number;
    capacity: number;
    seatsTaken: number;
    waitingSize:number;
    registrationList: AdminWorkshopRegistrationType[];
}

export interface AdminWorkshopSummaryType {
    workshopList: AdminWorkshopType[];
}

export interface AdminLoginInput {
    password:string;
}

export interface ChangeCapacityType {
    accessToken:string;
    workshopId:string;
    capacity:number;
}

export interface RegistrationCollisionType {
    name:string;
    email:string;
    workshopAName:string;
    workshopBName:string;
    statusA:RegistrationStatus;
    statusB:RegistrationStatus;
    astart:string;
    aend:string;
    bstart:string;
    bend:string;
    registrationIdA:string;
    registrationIdB:string;
}

export interface CollisionSummaryType {
    registrationCollisionList: RegistrationCollisionType[]
}