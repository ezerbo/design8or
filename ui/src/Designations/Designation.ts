import {User} from "../Users/User";

export interface Designation {
    id?: number;
    status?: string;
    designationDate?: string;
    userResponseDate?: string | null;
    user?: User;
    token?: string;
    declined?: boolean;
    pending?: boolean;
    stale?: boolean;
    accepted?: boolean;
}