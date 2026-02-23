import React from "react";
import {Assignment} from "./Designation";
import {httpGet} from "../../Commons/Http.util";
import {format} from "date-fns";
import {GET_CURRENT_DESIGNATION} from "../../Commons/Paths";
import {Card, Divider, makeStyles, tokens, Badge} from "@fluentui/react-components";
import {Calendar24Regular, ClipboardTaskListLtr24Regular, Mail24Regular, Person24Regular} from "@fluentui/react-icons";

const useStyles = makeStyles({
    card: {
        maxWidth: '400px',
        padding: '16px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
        '& svg': {
            color: '#0078d4',
        },
    },
    title: {
        fontSize: '18px',
        fontWeight: 300,
        marginBottom: '16px',
        color: '#0078d4',
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

    const [designation, setDesignation] = React.useState<Assignment>();

    React.useEffect(() => {
        httpGet<Assignment>(GET_CURRENT_DESIGNATION)
            .then((designation) => setDesignation(designation))
            .catch((error) => console.error('No current designation found', error));
    }, []);

    return (
        <div>
            {
                designation && (
                    <Card className={styles.card}>
                        <div className={styles.title}>Current Lead</div>
                        <Divider className={styles.divider} />
                        <div className={styles.content}>
                            <div className={styles.section}>
                                <ClipboardTaskListLtr24Regular />
                                <Badge appearance="filled" color="success">
                                    {designation.designationStatus}
                                </Badge>
                                {designation.designationType && (
                                    <Badge appearance="outline">
                                        {designation.designationType}
                                    </Badge>
                                )}
                            </div>
                            {designation.designatedAt && (
                                <div className={styles.section}>
                                    <Calendar24Regular />
                                    <span className={styles.label}>Designated:</span>
                                    <span>{format(new Date(designation.designatedAt), 'yyyy-MM-dd HH:mm')}</span>
                                </div>
                            )}
                            {designation.respondedAt && (
                                <div className={styles.section}>
                                    <Calendar24Regular />
                                    <span className={styles.label}>Accepted:</span>
                                    <span>{format(new Date(designation.respondedAt), 'yyyy-MM-dd HH:mm')}</span>
                                </div>
                            )}

                            <Divider className={styles.divider} />

                            <div className={styles.section}>
                                <Person24Regular />
                                <span>{designation.user.firstName} {designation.user.lastName}</span>
                            </div>
                            <div className={styles.section}>
                                <Mail24Regular />
                                <span>{designation.user.emailAddress}</span>
                            </div>
                        </div>
                    </Card>
                )
            }
        </div>
    );
}
