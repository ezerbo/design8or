import React, {useEffect, useState} from 'react';
import {useNavigate, useSearchParams} from 'react-router-dom';
import {
    Card,
    Spinner,
    makeStyles,
    tokens,
    Toast,
    ToastTitle,
    Toaster,
    useToastController,
    useId
} from '@fluentui/react-components';
import {CheckmarkCircleRegular, DismissCircleRegular} from '@fluentui/react-icons';
import axios from 'axios';
import {API_BASE_URL} from '../Commons/Paths';

const useStyles = makeStyles({
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: tokens.colorNeutralBackground3,
    },
    card: {
        padding: '32px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
        textAlign: 'center',
        minWidth: '400px',
    },
    icon: {
        fontSize: '64px',
        marginBottom: '16px',
    },
    successIcon: {
        color: '#107c10',
    },
    errorIcon: {
        color: '#d13438',
    },
    title: {
        fontSize: '20px',
        fontWeight: 600,
        color: '#323130',
        marginBottom: '8px',
    },
    message: {
        fontSize: '14px',
        color: '#605e5c',
        marginBottom: '24px',
    },
});

export const DesignationResponse: React.FunctionComponent = () => {
    const styles = useStyles();
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const toasterId = useId('toaster');
    const {dispatchToast} = useToastController(toasterId);
    const [processing, setProcessing] = useState(true);
    const [success, setSuccess] = useState(false);
    const [errorMessage, setErrorMessage] = useState<string>('');

    useEffect(() => {
        const processDesignationResponse = async () => {
            // Extract parameters from URL
            const designationId = searchParams.get('designationId');
            const emailAddress = searchParams.get('emailAddress');
            const answer = searchParams.get('answer');

            if (!designationId || !emailAddress || !answer) {
                setErrorMessage('Invalid designation response link');
                setProcessing(false);
                setTimeout(() => navigate('/'), 3000);
                return;
            }

            try {
                // Make API call to backend
                await axios.get(
                    `${API_BASE_URL}/designations/${designationId}/response`,
                    {
                        params: {
                            emailAddress,
                            answer
                        }
                    }
                );

                setSuccess(true);
                setProcessing(false);

                // Show success notification
                const action = answer === 'ACCEPT' ? 'accepted' : 'declined';
                dispatchToast(
                    <Toast>
                        <ToastTitle>Designation {action} successfully</ToastTitle>
                    </Toast>,
                    {intent: 'success'}
                );

                // Redirect to home after 2 seconds
                setTimeout(() => navigate('/'), 2000);
            } catch (error: any) {
                console.error('Error processing designation response:', error);
                setSuccess(false);
                setProcessing(false);
                setErrorMessage(
                    error.response?.data?.message || 'Failed to process designation response'
                );

                dispatchToast(
                    <Toast>
                        <ToastTitle>Error processing designation response</ToastTitle>
                    </Toast>,
                    {intent: 'error'}
                );

                // Redirect to home after 3 seconds even on error
                setTimeout(() => navigate('/'), 3000);
            }
        };

        processDesignationResponse();
    }, [searchParams, navigate, dispatchToast]);

    return (
        <>
            <Toaster toasterId={toasterId} position="top-end" />
            <div className={styles.container}>
                <Card className={styles.card}>
                    {processing ? (
                        <>
                            <Spinner size="extra-large" />
                            <div className={styles.title}>Processing your response...</div>
                            <div className={styles.message}>Please wait</div>
                        </>
                    ) : success ? (
                        <>
                            <CheckmarkCircleRegular className={`${styles.icon} ${styles.successIcon}`} />
                            <div className={styles.title}>Response Recorded</div>
                            <div className={styles.message}>
                                Your designation response has been recorded successfully.
                                Redirecting to home page...
                            </div>
                        </>
                    ) : (
                        <>
                            <DismissCircleRegular className={`${styles.icon} ${styles.errorIcon}`} />
                            <div className={styles.title}>Error</div>
                            <div className={styles.message}>
                                {errorMessage}
                                <br />
                                Redirecting to home page...
                            </div>
                        </>
                    )}
                </Card>
            </div>
        </>
    );
};
