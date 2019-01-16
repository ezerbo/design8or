export interface Pool {
    id: number
    startDate: Date
    endDate: Date
}

export interface Pools {
    current: Pool
    past: Pool[]
    currentPoolProgress: number
    currentPoolParticipantsCount: number
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

export interface Subscription {
    id?: number
    endpoint: string
    auth: string
    p256dh: string
}

export interface Designation {
    id: number
    status: string
    designationDate: Date;
    current: boolean
    user: User
}

export interface Assignment {
    id: number
    assignmentDate: Date
    user: User
    pool: Pool
}

export interface DesignationResponse {
    token: string
    response: string
    emailAddress: string
}

export const WSEndpoint = {
    pools: '/pools',
    designations: '/designations',
    assignments: '/assignments',
    parameters: '/parameters'
}