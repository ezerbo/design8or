import { Time } from "../../node_modules/@angular/common";

export interface Pool {
    id: number
    startDate: Date
    endDate: Date
}

export interface Pools {
    current: Pool
    past: Pool[]
}

export interface User {
    id?: number
    firstName: string
    lastName: string
    emailAddress: string
    lead?: boolean
}

export interface Error {
    description: string
    message: string
}

export interface Parameter {
    rotationTime: string;
}