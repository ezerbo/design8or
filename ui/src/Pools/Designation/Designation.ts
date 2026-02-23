import { DesignationStatus } from "./DesignationStatus";
import { DesignationType } from "./DesignationType";
import { User } from "../../Users/User";

export interface Assignment {
    id: number;
    user: User;
    assignmentDate: string;
    designationStatus?: DesignationStatus;
    designationType?: DesignationType;
    designatedAt?: string;
    respondedAt?: string;
}

// Keep Designation as an alias for backward compatibility
export type Designation = Assignment;
