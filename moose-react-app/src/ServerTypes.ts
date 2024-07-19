export enum WorkshopStatus {
    OPEN = "OPEN",
    NOT_OPEN = "NOT_OPEN",
    FULL = "FULL",
    CLOSED = "CLOSED"
}

export interface WorkshopType {
    id: string,
    name: string,
    workshopstatus: WorkshopStatus
}

export interface AddParticipantInput {
    name: String,
    email: String
}
