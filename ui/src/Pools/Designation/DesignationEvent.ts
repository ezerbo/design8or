import { DesignationStatus } from "./DesignationStatus";
import { DesignationType } from "./DesignationType";
import { User } from "../../Users/User";

export interface DesignationEvent {
    poolId: number;
    user?: User;
    status: DesignationStatus;
    designationType: DesignationType;
}
