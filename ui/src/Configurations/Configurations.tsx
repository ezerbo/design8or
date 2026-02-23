import React, {useEffect, useState} from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableCellLayout,
    TableHeader,
    TableHeaderCell,
    TableRow,
    Button,
    Card,
    Divider,
    makeStyles,
    tokens,
    Toast,
    ToastTitle,
    Toaster,
    useToastController,
    useId,
    Input,
    Dialog,
    DialogTrigger,
    DialogSurface,
    DialogTitle,
    DialogBody,
    DialogActions,
    DialogContent
} from "@fluentui/react-components";
import {
    SettingsRegular,
    EditRegular,
    SaveRegular,
    DismissRegular
} from "@fluentui/react-icons";
import {httpGet} from "../Commons/Http.util";
import {API_BASE_URL} from "../Commons/Paths";
import axios from "axios";

const useStyles = makeStyles({
    card: {
        padding: '16px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
        marginBottom: '16px',
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
    section: {
        marginBottom: '32px',
    },
    configTable: {
        tableLayout: 'fixed',
        width: '100%',
    },
});

interface Configuration {
    id: number;
    key: string;
    value: string;
    description: string;
}

const configColumns = [
    {columnKey: "key", label: "Key"},
    {columnKey: "value", label: "Value"},
    {columnKey: "description", label: "Description"},
    {columnKey: "actions", label: "Actions"}
];

export const Configurations: React.FunctionComponent = () => {
    const styles = useStyles();
    const toasterId = useId("toaster");
    const {dispatchToast} = useToastController(toasterId);
    const [configurations, setConfigurations] = useState<Configuration[]>([]);
    const [editingConfig, setEditingConfig] = useState<Configuration | null>(null);
    const [editValue, setEditValue] = useState("");
    const [dialogOpen, setDialogOpen] = useState(false);

    const fetchConfigurations = async () => {
        try {
            const configs = await httpGet<Configuration[]>(`${API_BASE_URL}/configurations`);
            setConfigurations(configs);
        } catch (error) {
            console.error('Error fetching configurations:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error fetching configurations</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    useEffect(() => {
        fetchConfigurations();
    }, []);

    const handleEdit = (config: Configuration) => {
        setEditingConfig(config);
        setEditValue(config.value);
        setDialogOpen(true);
    };

    const handleSave = async () => {
        if (!editingConfig) return;

        try {
            await axios.put(`${API_BASE_URL}/configurations/${editingConfig.id}`, {
                value: editValue
            });
            dispatchToast(
                <Toast>
                    <ToastTitle>Configuration updated successfully</ToastTitle>
                </Toast>,
                {intent: 'success'}
            );
            fetchConfigurations();
            setDialogOpen(false);
            setEditingConfig(null);
            setEditValue("");
        } catch (error) {
            console.error('Error updating configuration:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error updating configuration</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    const handleCancel = () => {
        setDialogOpen(false);
        setEditingConfig(null);
        setEditValue("");
    };

    return (
        <>
            <Toaster toasterId={toasterId} position="top-end"/>

            <div className={styles.section}>
                <Card className={styles.card}>
                    <div className={styles.title}>
                        <SettingsRegular/>
                        Application Configurations
                    </div>
                    <Divider className={styles.divider}/>

                    <Table arial-label="Configurations table" className={styles.configTable}>
                        <TableHeader>
                            <TableRow>
                                <TableHeaderCell style={{width: '25%'}}>
                                    Key
                                </TableHeaderCell>
                                <TableHeaderCell style={{width: '30%'}}>
                                    Value
                                </TableHeaderCell>
                                <TableHeaderCell style={{width: '35%'}}>
                                    Description
                                </TableHeaderCell>
                                <TableHeaderCell style={{width: '10%'}}>
                                    Actions
                                </TableHeaderCell>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {configurations.map((config) => (
                                <TableRow key={config.id}>
                                    <TableCell>
                                        <TableCellLayout>
                                            <strong>{config.key}</strong>
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            <div style={{
                                                maxWidth: '100%',
                                                overflow: 'hidden',
                                                textOverflow: 'ellipsis',
                                                whiteSpace: 'nowrap'
                                            }} title={config.value}>
                                                {config.value}
                                            </div>
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            {config.description}
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <Button
                                            size="small"
                                            appearance="subtle"
                                            icon={<EditRegular/>}
                                            onClick={() => handleEdit(config)}
                                        >
                                            Edit
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </Card>
            </div>

            <Dialog open={dialogOpen} onOpenChange={(event, data) => setDialogOpen(data.open)}>
                <DialogSurface>
                    <DialogBody>
                        <DialogTitle>Edit Configuration</DialogTitle>
                        <DialogContent>
                            {editingConfig && (
                                <>
                                    <div style={{marginBottom: '16px'}}>
                                        <strong>Key:</strong> {editingConfig.key}
                                    </div>
                                    <div style={{marginBottom: '16px'}}>
                                        <strong>Description:</strong> {editingConfig.description}
                                    </div>
                                    <div>
                                        <label style={{
                                            display: 'block',
                                            marginBottom: '8px',
                                            fontWeight: 500
                                        }}>
                                            Value:
                                        </label>
                                        <Input
                                            value={editValue}
                                            onChange={(e) => setEditValue(e.target.value)}
                                            style={{width: '100%'}}
                                        />
                                    </div>
                                </>
                            )}
                        </DialogContent>
                        <DialogActions>
                            <Button
                                appearance="secondary"
                                onClick={handleCancel}
                                icon={<DismissRegular/>}
                            >
                                Cancel
                            </Button>
                            <Button
                                appearance="primary"
                                onClick={handleSave}
                                icon={<SaveRegular/>}
                            >
                                Save
                            </Button>
                        </DialogActions>
                    </DialogBody>
                </DialogSurface>
            </Dialog>
        </>
    );
};
