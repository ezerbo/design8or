import React, {useState, useEffect} from "react";
import {
    Dialog,
    DialogTrigger,
    DialogSurface,
    DialogTitle,
    DialogBody,
    DialogActions,
    DialogContent,
    Button,
    Input,
    Field,
    makeStyles
} from "@fluentui/react-components";
import {User} from "./User";
import {API_BASE_URL} from "../Commons/Paths";
import axios from "axios";

const useStyles = makeStyles({
    content: {
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
    },
});

interface UserDialogProps {
    user?: User;
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSuccess: (message: string) => void;
}

export const UserDialog: React.FunctionComponent<UserDialogProps> = ({user, open, onOpenChange, onSuccess}) => {
    const styles = useStyles();
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [emailAddress, setEmailAddress] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user) {
            setFirstName(String(user.firstName));
            setLastName(String(user.lastName));
            setEmailAddress(String(user.emailAddress));
        } else {
            setFirstName('');
            setLastName('');
            setEmailAddress('');
        }
    }, [user, open]);

    const handleSubmit = async () => {
        setLoading(true);
        try {
            const userData = {
                firstName,
                lastName,
                emailAddress
            };

            if (user) {
                // Update existing user
                await axios.put(`${API_BASE_URL}/users/${(user as any).id}`, userData);
                onSuccess('User updated successfully');
            } else {
                // Create new user
                await axios.post(`${API_BASE_URL}/users`, userData);
                onSuccess('User created successfully');
            }
            onOpenChange(false);
        } catch (error) {
            console.error('Error saving user:', error);
            onSuccess('Error saving user');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={(event, data) => onOpenChange(data.open)}>
            <DialogSurface>
                <DialogBody>
                    <DialogTitle>{user ? 'Edit User' : 'Create New User'}</DialogTitle>
                    <DialogContent className={styles.content}>
                        <Field label="First Name" required>
                            <Input
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                                placeholder="Enter first name"
                            />
                        </Field>
                        <Field label="Last Name" required>
                            <Input
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                                placeholder="Enter last name"
                            />
                        </Field>
                        <Field label="Email Address" required>
                            <Input
                                type="email"
                                value={emailAddress}
                                onChange={(e) => setEmailAddress(e.target.value)}
                                placeholder="Enter email address"
                            />
                        </Field>
                    </DialogContent>
                    <DialogActions>
                        <DialogTrigger disableButtonEnhancement>
                            <Button appearance="secondary" disabled={loading}>Cancel</Button>
                        </DialogTrigger>
                        <Button
                            appearance="primary"
                            onClick={handleSubmit}
                            disabled={loading || !firstName || !lastName || !emailAddress}
                        >
                            {loading ? 'Saving...' : user ? 'Update' : 'Create'}
                        </Button>
                    </DialogActions>
                </DialogBody>
            </DialogSurface>
        </Dialog>
    );
};
