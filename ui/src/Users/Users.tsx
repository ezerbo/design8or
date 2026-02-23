import React, {useEffect} from "react";
import {MailRegular, SlideTextRegular, AddRegular, EditRegular, DeleteRegular} from "@fluentui/react-icons";
import {User} from "./User";
import {httpGetWithHeaders} from "../Commons/Http.util";
import {PaginationHeaders} from "../Commons/PaginationHeaders";
import {API_BASE_URL} from "../Commons/Paths";
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
    TableSelectionCell,
    Toast,
    ToastTitle,
    Toaster,
    useToastController,
    useId,
    Dialog,
    DialogTrigger,
    DialogSurface,
    DialogTitle,
    DialogBody,
    DialogActions,
    DialogContent
} from "@fluentui/react-components";
import {UserDialog} from "./UserDialog";
import axios from "axios";
import './Users.css';

const useStyles = makeStyles({
    card: {
        padding: '16px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
    },
    header: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: '16px',
    },
    title: {
        fontSize: '18px',
        fontWeight: 300,
        color: '#0078d4',
    },
    divider: {
        marginBottom: '16px',
    },
    tableContainer: {
        flex: 1,
    },
    deleteButton: {
        backgroundColor: '#d13438',
        color: 'white',
        ':hover': {
            backgroundColor: '#a72b2f',
        },
    },
    deleteIconButton: {
        color: '#d13438',
        ':hover': {
            color: '#a72b2f',
        },
    },
});

const columns = [
    {columnKey: "firstName", label: "First Name", icon: <SlideTextRegular/>},
    {columnKey: "lastName", label: "Last Name", icon: <SlideTextRegular/>},
    {columnKey: "emailAddress", label: "Email Address", icon: <MailRegular/>}
];

const getItems = (users: User[]) => {
    return users.map(user => {
        return {
            firstName: {label: user.firstName},
            lastName: {label: user.lastName},
            emailAddress: {label: user.emailAddress}
        }
    });
}

const DEFAULT_PAGE_SIZE = 10;

interface PaginationInfo {
    totalCount: number;
    pageNumber: number;
    pageSize: number;
    totalPages: number;
}

export const Users: React.FunctionComponent = () => {
    const styles = useStyles();
    const toasterId = useId("toaster");
    const {dispatchToast} = useToastController(toasterId);
    const [items, setItems] = React.useState<any[]>([]);
    const [users, setUsers] = React.useState<User[]>([]);
    const [currentPage, setCurrentPage] = React.useState(0);
    const [pageSize] = React.useState(DEFAULT_PAGE_SIZE);
    const [selectedRows, setSelectedRows] = React.useState<Set<number>>(new Set());
    const [dialogOpen, setDialogOpen] = React.useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = React.useState(false);
    const [editingUser, setEditingUser] = React.useState<User | undefined>(undefined);
    const [deletingUser, setDeletingUser] = React.useState<User | undefined>(undefined);
    const [paginationInfo, setPaginationInfo] = React.useState<PaginationInfo>({
        totalCount: 0,
        pageNumber: 0,
        pageSize: DEFAULT_PAGE_SIZE,
        totalPages: 0
    });

    const fetchUsers = () => {
        const url = `${API_BASE_URL}/users?page=${currentPage}&size=${pageSize}`;
        httpGetWithHeaders<User[]>(url)
            .then((response) => {
                setUsers(response.data);
                setItems(getItems(response.data));

                // Access headers using bracket notation (axios normalizes to lowercase)
                const totalCountHeader = response.headers[PaginationHeaders.TOTAL_COUNT];
                const pageNumberHeader = response.headers[PaginationHeaders.PAGE_NUMBER];
                const pageSizeHeader = response.headers[PaginationHeaders.PAGE_SIZE];
                const totalPagesHeader = response.headers[PaginationHeaders.TOTAL_PAGES];

                setPaginationInfo({
                    totalCount: parseInt(totalCountHeader || '0'),
                    pageNumber: parseInt(pageNumberHeader || '0'),
                    pageSize: parseInt(pageSizeHeader || String(DEFAULT_PAGE_SIZE)),
                    totalPages: parseInt(totalPagesHeader || '0')
                });
            });
    };

    useEffect(() => {
        fetchUsers();
    }, [currentPage, pageSize]);

    const handleOpenCreateDialog = () => {
        setEditingUser(undefined);
        setDialogOpen(true);
    };

    const handleOpenEditDialog = (user: User) => {
        setEditingUser(user);
        setDialogOpen(true);
    };

    const handleDialogSuccess = (message: string) => {
        dispatchToast(
            <Toast>
                <ToastTitle>{message}</ToastTitle>
            </Toast>,
            {intent: message.includes('Error') ? 'error' : 'success'}
        );
        fetchUsers();
    };

    const handleOpenDeleteDialog = (user: User) => {
        setDeletingUser(user);
        setDeleteDialogOpen(true);
    };

    const handleDeleteUser = async () => {
        if (!deletingUser?.id) return;

        try {
            await axios.delete(`${API_BASE_URL}/users/${deletingUser.id}`);
            dispatchToast(
                <Toast>
                    <ToastTitle>User deleted successfully</ToastTitle>
                </Toast>,
                {intent: 'success'}
            );
            setDeleteDialogOpen(false);
            fetchUsers();
        } catch (error) {
            console.error('Error deleting user:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error deleting user</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    const totalPages = paginationInfo.totalPages;

    const handlePreviousPage = () => {
        setCurrentPage((prev) => Math.max(0, prev - 1));
    };

    const handleNextPage = () => {
        setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1));
    };

    const handleFirstPage = () => {
        setCurrentPage(0);
    };

    const handleLastPage = () => {
        setCurrentPage(totalPages - 1);
    };

    const startItem = currentPage * pageSize + 1;
    const endItem = Math.min((currentPage + 1) * pageSize, paginationInfo.totalCount);

    const toggleRow = (index: number) => {
        const newSelection = new Set(selectedRows);
        if (newSelection.has(index)) {
            newSelection.delete(index);
        } else {
            newSelection.add(index);
        }
        setSelectedRows(newSelection);
    };

    const toggleAllRows = () => {
        if (selectedRows.size === items.length) {
            setSelectedRows(new Set());
        } else {
            setSelectedRows(new Set(items.map((_, i) => i)));
        }
    };

    const allRowsSelected = selectedRows.size === items.length && items.length > 0;
    const someRowsSelected = selectedRows.size > 0 && selectedRows.size < items.length;

    return (
        <>
            <Toaster toasterId={toasterId} position="top-end" />
            <Card className={styles.card}>
                <div className={styles.header}>
                    <div className={styles.title}>Users</div>
                    <div style={{display: 'flex', gap: '8px'}}>
                        <Button
                            appearance="secondary"
                            icon={<EditRegular />}
                            onClick={() => {
                                const selectedIndex = Array.from(selectedRows)[0];
                                if (selectedIndex !== undefined) {
                                    handleOpenEditDialog(users[selectedIndex]);
                                }
                            }}
                            disabled={selectedRows.size !== 1}
                        >
                            Edit
                        </Button>
                        <Button
                            appearance="secondary"
                            icon={<DeleteRegular />}
                            onClick={() => {
                                const selectedIndex = Array.from(selectedRows)[0];
                                if (selectedIndex !== undefined) {
                                    handleOpenDeleteDialog(users[selectedIndex]);
                                }
                            }}
                            disabled={selectedRows.size === 0}
                            style={{
                                backgroundColor: selectedRows.size === 0 ? undefined : '#d13438',
                                color: selectedRows.size === 0 ? undefined : 'white',
                                borderColor: selectedRows.size === 0 ? undefined : '#d13438'
                            }}
                        >
                            Delete
                        </Button>
                        <Button
                            appearance="primary"
                            icon={<AddRegular />}
                            onClick={handleOpenCreateDialog}
                        >
                            New User
                        </Button>
                    </div>
                </div>
                <Divider className={styles.divider} />
            <div className={styles.tableContainer}>
                <Table arial-label="Users table">
                    <TableHeader>
                        <TableRow>
                            <TableSelectionCell
                                checked={
                                    allRowsSelected ? true : someRowsSelected ? "mixed" : false
                                }
                                onClick={toggleAllRows}
                            />
                            {columns.map((column) => (
                                <TableHeaderCell key={column.columnKey}>
                                    <TableCellLayout media={column.icon}>
                                        {column.label}
                                    </TableCellLayout>
                                </TableHeaderCell>
                            ))}
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {items.map((item, i) => (
                            <TableRow key={i}>
                                <TableSelectionCell
                                    checked={selectedRows.has(i)}
                                    onClick={() => toggleRow(i)}
                                />
                                <TableCell>
                                    <TableCellLayout>
                                        {item.firstName.label}
                                    </TableCellLayout>
                                </TableCell>
                                <TableCell>
                                    <TableCellLayout>
                                        {item.lastName.label}
                                    </TableCellLayout>
                                </TableCell>
                                <TableCell>
                                    <TableCellLayout>
                                        {item.emailAddress.label}
                                    </TableCellLayout>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
                {totalPages > 1 && (
                    <div className="paginationContainer">
                        <div className="pageInfo">
                            Showing {startItem} - {endItem} of {paginationInfo.totalCount}
                        </div>
                        <div className="paginationControls">
                            <Button
                                size="small"
                                onClick={handleFirstPage}
                                disabled={currentPage === 0}
                            >
                                First
                            </Button>
                            <Button
                                size="small"
                                onClick={handlePreviousPage}
                                disabled={currentPage === 0}
                            >
                                Previous
                            </Button>
                            <span className="pageInfo">
                                Page {currentPage + 1} of {totalPages}
                            </span>
                            <Button
                                size="small"
                                onClick={handleNextPage}
                                disabled={currentPage >= totalPages - 1}
                            >
                                Next
                            </Button>
                            <Button
                                size="small"
                                onClick={handleLastPage}
                                disabled={currentPage >= totalPages - 1}
                            >
                                Last
                            </Button>
                        </div>
                    </div>
                )}
            </div>
            <UserDialog
                user={editingUser}
                open={dialogOpen}
                onOpenChange={setDialogOpen}
                onSuccess={handleDialogSuccess}
            />
            <Dialog open={deleteDialogOpen} onOpenChange={(_event, data) => setDeleteDialogOpen(data.open)}>
                <DialogSurface>
                    <DialogBody>
                        <DialogTitle>Delete User</DialogTitle>
                        <DialogContent>
                            Are you sure you want to delete {deletingUser?.firstName} {deletingUser?.lastName}?
                            This action cannot be undone.
                        </DialogContent>
                        <DialogActions>
                            <DialogTrigger disableButtonEnhancement>
                                <Button appearance="secondary">Cancel</Button>
                            </DialogTrigger>
                            <Button className={styles.deleteButton} onClick={handleDeleteUser}>
                                Delete
                            </Button>
                        </DialogActions>
                    </DialogBody>
                </DialogSurface>
            </Dialog>
        </Card>
        </>
    );
};