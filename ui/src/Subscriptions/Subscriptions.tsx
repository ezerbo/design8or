import React, {useEffect, useState} from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableCellLayout,
    TableHeader,
    TableHeaderCell,
    TableRow,
    Card,
    Divider,
    makeStyles,
    tokens,
    Toast,
    ToastTitle,
    Toaster,
    useToastController,
    useId,
    Button,
    Badge,
} from "@fluentui/react-components";
import {PlugDisconnectedRegular, PlugConnectedRegular, CheckmarkCircleRegular, DismissCircleRegular} from "@fluentui/react-icons";
import {httpGet} from "../Commons/Http.util";
import {API_BASE_URL} from "../Commons/Paths";
import {NotificationService} from "../Commons/NotificationService";

const useStyles = makeStyles({
    card: {
        padding: '16px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
    },
    title: {
        fontSize: '18px',
        fontWeight: 300,
        color: '#0078d4',
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        marginBottom: '16px',
    },
    divider: {
        marginBottom: '16px',
    },
});

interface Subscription {
    id: number;
    endpoint: string;
    auth: string;
    p256dh: string;
}

const subscriptionColumns = [
    {columnKey: "id", label: "ID"},
    {columnKey: "endpoint", label: "Endpoint"},
    {columnKey: "auth", label: "Auth"},
    {columnKey: "p256dh", label: "P256DH"}
];

export const Subscriptions: React.FunctionComponent = () => {
    const styles = useStyles();
    const toasterId = useId("toaster");
    const {dispatchToast} = useToastController(toasterId);
    const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
    const [isSubscribed, setIsSubscribed] = useState(false);
    const [isChecking, setIsChecking] = useState(true);

    const fetchSubscriptions = async () => {
        try {
            const subs = await httpGet<Subscription[]>(`${API_BASE_URL}/subscriptions?size=100&page=0`);
            setSubscriptions(subs);
        } catch (error) {
            console.error('Error fetching subscriptions:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error fetching subscriptions</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    const checkSubscriptionStatus = async () => {
        setIsChecking(true);
        try {
            const subscribed = await NotificationService.isSubscribed();
            setIsSubscribed(subscribed);
        } catch (error) {
            console.error('Error checking subscription status:', error);
        } finally {
            setIsChecking(false);
        }
    };

    const handleSubscribe = async () => {
        try {
            const success = await NotificationService.subscribe();
            if (success) {
                setIsSubscribed(true);
                dispatchToast(
                    <Toast>
                        <ToastTitle>Successfully subscribed to notifications</ToastTitle>
                    </Toast>,
                    {intent: 'success'}
                );
                // Refresh the subscription list
                await fetchSubscriptions();
            } else {
                dispatchToast(
                    <Toast>
                        <ToastTitle>Failed to subscribe. Permission may have been denied.</ToastTitle>
                    </Toast>,
                    {intent: 'error'}
                );
            }
        } catch (error) {
            console.error('Error subscribing:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error subscribing to notifications</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    const handleUnsubscribe = async () => {
        try {
            const success = await NotificationService.unsubscribe();
            if (success) {
                setIsSubscribed(false);
                dispatchToast(
                    <Toast>
                        <ToastTitle>Successfully unsubscribed from notifications</ToastTitle>
                    </Toast>,
                    {intent: 'success'}
                );
            }
        } catch (error) {
            console.error('Error unsubscribing:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error unsubscribing from notifications</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    useEffect(() => {
        fetchSubscriptions();
        checkSubscriptionStatus();
    }, []);

    return (
        <>
            <Toaster toasterId={toasterId} position="top-end"/>
            <Card className={styles.card}>
                <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px'}}>
                    <div className={styles.title}>
                        {isSubscribed ? <PlugConnectedRegular/> : <PlugDisconnectedRegular/>}
                        Browser Push Notification Subscriptions
                    </div>
                    <div style={{display: 'flex', gap: '8px', alignItems: 'center'}}>
                        {!isChecking && (
                            <>
                                <Badge
                                    appearance="filled"
                                    color={isSubscribed ? 'success' : 'danger'}
                                    icon={isSubscribed ? <CheckmarkCircleRegular /> : <DismissCircleRegular />}
                                >
                                    {isSubscribed ? 'Subscribed' : 'Not Subscribed'}
                                </Badge>
                                {isSubscribed ? (
                                    <Button
                                        appearance="secondary"
                                        onClick={handleUnsubscribe}
                                    >
                                        Unsubscribe
                                    </Button>
                                ) : (
                                    <Button
                                        appearance="primary"
                                        onClick={handleSubscribe}
                                    >
                                        Subscribe to Notifications
                                    </Button>
                                )}
                            </>
                        )}
                    </div>
                </div>
                <Divider className={styles.divider}/>

                {subscriptions.length === 0 ? (
                    <div style={{textAlign: 'center', padding: '32px', color: '#605e5c'}}>
                        No subscriptions found
                    </div>
                ) : (
                    <Table arial-label="Subscriptions table">
                        <TableHeader>
                            <TableRow>
                                {subscriptionColumns.map((column) => (
                                    <TableHeaderCell key={column.columnKey}>
                                        {column.label}
                                    </TableHeaderCell>
                                ))}
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {subscriptions.map((sub) => (
                                <TableRow key={sub.id}>
                                    <TableCell>
                                        <TableCellLayout>
                                            {sub.id}
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            <div style={{
                                                maxWidth: '300px',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                whiteSpace: 'nowrap'
                                            }}>
                                                {sub.endpoint}
                                            </div>
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            <div style={{
                                                maxWidth: '200px',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                whiteSpace: 'nowrap'
                                            }}>
                                                {sub.auth}
                                            </div>
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            <div style={{
                                                maxWidth: '200px',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                whiteSpace: 'nowrap'
                                            }}>
                                                {sub.p256dh}
                                            </div>
                                        </TableCellLayout>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                )}
            </Card>
        </>
    );
};
