import React from "react";
import {Designation} from "./Designation";
import {httpGet} from "../../Commons/Http.util";
import {format} from "date-fns";
import {GET_CURRENT_DESIGNATION} from "../../Commons/Paths";
import {Card, Divider, makeStyles, tokens} from "@fluentui/react-components";
import {Calendar24Regular, ClipboardTaskListLtr24Regular, Mail24Regular, Person24Regular} from "@fluentui/react-icons";

const useStyles = makeStyles({
    card: {
        maxWidth: '400px',
        padding: '16px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
    },
    section: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        marginBottom: '8px',
    },
    divider: {
        margin: '16px 0',
    },
    label: {
        fontWeight: 500,
    },
    content: {
        display: 'flex',
        flexDirection: 'column',
        gap: '4px',
    }
});
export const PendingDesignation: React.FunctionComponent = () => {
    const styles = useStyles();

    const [designation, setDesignation] = React.useState<Designation>();

    React.useEffect(() => {
        httpGet<Designation>(GET_CURRENT_DESIGNATION)
            .then((designation) => setDesignation(designation));
    }, []);

    return (
        <div>
            {
                designation && (
                    <Card className={styles.card}>
                        <div className={styles.content}>
                            <div className={styles.section}>
                                <ClipboardTaskListLtr24Regular />
                                <span className={styles.label}>Status:</span>
                                <span>{designation.status}</span>
                            </div>
                            <div className={styles.section}>
                                <Calendar24Regular />
                                <span className={styles.label}>Designation Date:</span>
                                <span>{format(designation.designationDate, 'yyyy-MM-dd')}</span>
                            </div>
                            {designation.reassignmentDate && (
                                <div className={styles.section}>
                                    <Calendar24Regular />
                                    <span className={styles.label}>Reassignment Date:</span>
                                    <span>{format(designation.reassignmentDate, 'yyyy-MM-dd')}</span>
                                </div>
                            )}

                            <Divider className={styles.divider} />

                            <div className={styles.section}>
                                <Person24Regular />
                                <span className={styles.label}>First Name:</span>
                                <span>{designation.user.firstName}</span>
                            </div>
                            <div className={styles.section}>
                                <Person24Regular />
                                <span className={styles.label}>Last Name:</span>
                                <span>{designation.user.lastName}</span>
                            </div>
                            <div className={styles.section}>
                                <Mail24Regular />
                                <span className={styles.label}>Email Address:</span>
                                <span>{designation.user.emailAddress}</span>
                            </div>
                        </div>
                    </Card>
                )
            }
        </div>
    );
}