import {User} from "../Users/User";

export interface Pool {
    startDate: Date;
    enDate: Date;
    lead?: User;
}