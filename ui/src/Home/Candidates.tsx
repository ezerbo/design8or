import React, {useEffect, useState, useMemo} from "react";
import {User} from "../Users/User";
import {Pool} from "../Pools/Pool";
import {httpGet} from "../Commons/Http.util";
import {API_BASE_URL, getPoolAssignmentsPath} from "../Commons/Paths";
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
    TableSelectionCell,
    Dialog,
    DialogSurface,
    DialogTitle,
    DialogBody,
    DialogContent,
    DialogActions,
    Tooltip
} from "@fluentui/react-components";
import {MailRegular, SlideTextRegular, PersonAvailableRegular, CalendarRegular, PeopleRegular, PersonRegular, ChartMultipleRegular, PeopleListRegular} from "@fluentui/react-icons";
import axios from "axios";
import {format} from "date-fns";
import {Assignment} from "../Pools/Designation/Designation";
import {DesignationStatus} from "../Pools/Designation/DesignationStatus";
import {GET_CURRENT_DESIGNATION} from "../Commons/Paths";
import './Candidates.css';

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
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
    },
    divider: {
        marginBottom: '16px',
    },
    tableContainer: {
        flex: 1,
    },
    noPool: {
        textAlign: 'center',
        padding: '32px',
        color: '#605e5c',
    },
    statsCard: {
        padding: '16px',
        boxShadow: tokens.shadow4,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
        marginBottom: '16px',
    },
    statsGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '16px',
    },
    statItem: {
        display: 'flex',
        alignItems: 'center',
        gap: '12px',
    },
    statIcon: {
        color: '#0078d4',
        fontSize: '24px',
    },
    statContent: {
        display: 'flex',
        flexDirection: 'column',
    },
    statLabel: {
        fontSize: '12px',
        color: '#605e5c',
        fontWeight: 500,
    },
    statValue: {
        fontSize: '18px',
        color: '#323130',
        fontWeight: 600,
    },
    actionButtonContainer: {
        display: 'flex',
        justifyContent: 'flex-end',
        marginBottom: '16px',
    },
    selectionCell: {
        width: '48px',
    },
});

const columns = [
    {columnKey: "firstName", label: "First Name", icon: <SlideTextRegular/>},
    {columnKey: "lastName", label: "Last Name", icon: <SlideTextRegular/>},
    {columnKey: "emailAddress", label: "Email Address", icon: <MailRegular/>}
];

const DEFAULT_PAGE_SIZE = 5;

export const Candidates: React.FunctionComponent = () => {
    const styles = useStyles();
    const toasterId = useId("toaster");
    const {dispatchToast} = useToastController(toasterId);
    const [currentPool, setCurrentPool] = useState<Pool | null>(null);
    const [candidates, setCandidates] = useState<User[]>([]);
    const [currentDesignation, setCurrentDesignation] = useState<Assignment | null>(null);
    const [loading, setLoading] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [selectedRow, setSelectedRow] = useState<number | null>(null);
    const [participantsDialogOpen, setParticipantsDialogOpen] = useState(false);
    const [participants, setParticipants] = useState<Assignment[]>([]);
    const [participantsPage, setParticipantsPage] = useState(0);
    const participantsPageSize = 5;

    const fetchCurrentPool = async () => {
        try {
            const pools = await httpGet<Pool[]>(`${API_BASE_URL}/pools?size=10&page=0`);
            const activePool = pools.find(pool => pool.endDate === null);
            setCurrentPool(activePool || null);
            if (activePool) {
                fetchCandidates(activePool.id);
                fetchCurrentDesignation();
            }
        } catch (error) {
            console.error('Error fetching current pool:', error);
        }
    };

    const fetchCurrentDesignation = async () => {
        try {
            const designation = await httpGet<Assignment>(GET_CURRENT_DESIGNATION);
            setCurrentDesignation(designation);
        } catch (error) {
            console.error('Error fetching current designation:', error);
            setCurrentDesignation(null);
        }
    };

    const fetchCandidates = async (poolId: number) => {
        try {
            // Fetch all candidates from backend (size=100), then paginate client-side
            const candidatesList = await httpGet<User[]>(`${API_BASE_URL}/pools/${poolId}/candidates?size=100&page=0`);
            setCandidates(candidatesList);
        } catch (error) {
            console.error('Error fetching candidates:', error);
        }
    };

    const fetchParticipants = async () => {
        if (!currentPool) return;
        try {
            const assignments = await httpGet<Assignment[]>(getPoolAssignmentsPath(currentPool.id));
            setParticipants(assignments);
            setParticipantsPage(0); // Reset to first page
            setParticipantsDialogOpen(true);
        } catch (error) {
            console.error('Error fetching participants:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error fetching participants</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        }
    };

    useEffect(() => {
        fetchCurrentPool();
    }, []);

    const paginatedCandidates = useMemo(() => {
        const startIndex = currentPage * DEFAULT_PAGE_SIZE;
        const endIndex = startIndex + DEFAULT_PAGE_SIZE;
        return candidates.slice(startIndex, endIndex);
    }, [candidates, currentPage]);

    const totalPages = Math.ceil(candidates.length / DEFAULT_PAGE_SIZE);

    const paginatedParticipants = useMemo(() => {
        const startIndex = participantsPage * participantsPageSize;
        const endIndex = startIndex + participantsPageSize;
        return participants.slice(startIndex, endIndex);
    }, [participants, participantsPage, participantsPageSize]);

    const totalParticipantsPages = Math.ceil(participants.length / participantsPageSize);

    const handlePreviousPage = () => {
        setCurrentPage((prev) => Math.max(0, prev - 1));
        setSelectedRow(null);
    };

    const handleNextPage = () => {
        setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1));
        setSelectedRow(null);
    };

    const handleFirstPage = () => {
        setCurrentPage(0);
        setSelectedRow(null);
    };

    const handleLastPage = () => {
        setCurrentPage(totalPages - 1);
        setSelectedRow(null);
    };

    const toggleRow = (index: number) => {
        setSelectedRow(selectedRow === index ? null : index);
    };

    const startItem = currentPage * DEFAULT_PAGE_SIZE + 1;
    const endItem = Math.min((currentPage + 1) * DEFAULT_PAGE_SIZE, candidates.length);

    const handleDesignate = async () => {
        if (selectedRow === null) return;

        const selectedCandidate = paginatedCandidates[selectedRow];
        if (!selectedCandidate?.id) return;

        setLoading(true);
        try {
            await axios.post(`${API_BASE_URL}/users/${selectedCandidate.id}/designations`);
            dispatchToast(
                <Toast>
                    <ToastTitle>Designation request sent successfully</ToastTitle>
                </Toast>,
                {intent: 'success'}
            );
            // Refresh candidates and current designation after sending request
            if (currentPool) {
                fetchCandidates(currentPool.id);
                fetchCurrentDesignation();
            }
            setSelectedRow(null);
        } catch (error) {
            console.error('Error sending designation request:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error sending designation request</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        } finally {
            setLoading(false);
        }
    };

    if (!currentPool) {
        return (
            <>
                <Toaster toasterId={toasterId} position="top-end" />
                <Card className={styles.card}>
                    <div className={styles.title}>
                        <PeopleListRegular />
                        Candidates for Current Pool
                    </div>
                    <Divider className={styles.divider} />
                    <div className={styles.noPool}>
                        No active pool available. Please start a new pool.
                    </div>
                </Card>
            </>
        );
    }

    return (
        <>
            <Toaster toasterId={toasterId} position="top-end" />
            <Card className={styles.card}>
                <div className={styles.title}>
                    <ChartMultipleRegular />
                    Current Pool Statistics
                </div>
                <Divider className={styles.divider} />

                <Card className={styles.statsCard}>
                    <div className={styles.statsGrid}>
                        <div className={styles.statItem}>
                            <CalendarRegular className={styles.statIcon} />
                            <div className={styles.statContent}>
                                <span className={styles.statLabel}>Pool Started</span>
                                <span className={styles.statValue}>
                                    {format(new Date(currentPool.startDate), 'MMM dd, yyyy')}
                                </span>
                            </div>
                        </div>

                        <Tooltip content="Click to view participant details" relationship="description">
                            <div
                                className={styles.statItem}
                                onClick={fetchParticipants}
                                style={{cursor: 'pointer'}}
                            >
                                <PeopleRegular className={styles.statIcon} />
                                <div className={styles.statContent}>
                                    <span className={styles.statLabel}>Participants</span>
                                    <span className={styles.statValue} style={{color: '#0078d4'}}>
                                        {currentPool.participantCount ?? 0}
                                    </span>
                                </div>
                            </div>
                        </Tooltip>

                        <div className={styles.statItem}>
                            <PeopleRegular className={styles.statIcon} />
                            <div className={styles.statContent}>
                                <span className={styles.statLabel}>Candidates</span>
                                <span className={styles.statValue}>
                                    {candidates.length}
                                </span>
                            </div>
                        </div>

                        <div className={styles.statItem}>
                            <PersonRegular className={styles.statIcon} />
                            <div className={styles.statContent}>
                                <span className={styles.statLabel}>Current Lead</span>
                                <span className={styles.statValue}>
                                    {currentDesignation && currentDesignation.designationStatus === DesignationStatus.ACCEPTED
                                        ? `${currentDesignation.user.firstName} ${currentDesignation.user.lastName}`
                                        : 'None'
                                    }
                                </span>
                            </div>
                        </div>
                    </div>
                </Card>

                <div className={styles.title} style={{marginTop: '24px'}}>
                    <PeopleListRegular />
                    Candidates for Current Pool
                </div>
                <Divider className={styles.divider} />
                <div className={styles.actionButtonContainer}>
                    <Button
                        appearance="primary"
                        icon={<PersonAvailableRegular />}
                        onClick={handleDesignate}
                        disabled={selectedRow === null || loading}
                    >
                        Send Request
                    </Button>
                </div>
                <div className={styles.tableContainer}>
                    <Table arial-label="Candidates table">
                        <TableHeader>
                            <TableRow>
                                <TableHeaderCell className={styles.selectionCell} />
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
                            {paginatedCandidates.map((candidate, i) => (
                                <TableRow key={candidate.id}>
                                    <TableSelectionCell
                                        checked={selectedRow === i}
                                        onClick={() => toggleRow(i)}
                                        type="radio"
                                        className={styles.selectionCell}
                                    />
                                    <TableCell>
                                        <TableCellLayout>
                                            {candidate.firstName}
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            {candidate.lastName}
                                        </TableCellLayout>
                                    </TableCell>
                                    <TableCell>
                                        <TableCellLayout>
                                            {candidate.emailAddress}
                                        </TableCellLayout>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                    {candidates.length > 0 && (
                        <div className="paginationContainer">
                            <div className="pageInfo">
                                Showing {startItem} - {endItem} of {candidates.length}
                            </div>
                            {totalPages > 1 && (
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
                            )}
                        </div>
                    )}
                </div>

                <Dialog open={participantsDialogOpen} onOpenChange={(_, data) => setParticipantsDialogOpen(data.open)}>
                    <DialogSurface style={{maxWidth: '1200px', width: '90vw'}}>
                        <DialogBody>
                            <DialogTitle>Pool Participants</DialogTitle>
                            <DialogContent>
                                <Table style={{width: '100%'}}>
                                    <TableHeader>
                                        <TableRow>
                                            <TableHeaderCell>Name</TableHeaderCell>
                                            <TableHeaderCell>Email</TableHeaderCell>
                                            <TableHeaderCell>Designated At</TableHeaderCell>
                                            <TableHeaderCell>Accepted At</TableHeaderCell>
                                            <TableHeaderCell>Response Time</TableHeaderCell>
                                            <TableHeaderCell>Lead Duration</TableHeaderCell>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {paginatedParticipants.map((assignment, paginatedIndex) => {
                                            const acceptedAt = new Date(assignment.respondedAt!);
                                            const designatedAt = assignment.designatedAt ? new Date(assignment.designatedAt) : null;

                                            // Find actual index in full participants array
                                            const actualIndex = participants.findIndex(p => p.id === assignment.id);

                                            // Calculate response time (time from designation to acceptance)
                                            let responseTimeStr = '-';
                                            if (designatedAt) {
                                                const responseMs = Math.max(0, acceptedAt.getTime() - designatedAt.getTime());
                                                const responseHours = Math.floor(responseMs / (1000 * 60 * 60));
                                                const responseMinutes = Math.floor((responseMs % (1000 * 60 * 60)) / (1000 * 60));
                                                const responseSeconds = Math.floor((responseMs % (1000 * 60)) / 1000);

                                                if (responseHours > 0) {
                                                    responseTimeStr = `${responseHours}h ${responseMinutes}m`;
                                                } else if (responseMinutes > 0) {
                                                    responseTimeStr = `${responseMinutes}m ${responseSeconds}s`;
                                                } else {
                                                    responseTimeStr = `${responseSeconds}s`;
                                                }
                                            }

                                            // Calculate lead duration: participants are sorted by respondedAt DESC (most recent first)
                                            // For current lead (actualIndex 0): now - respondedAt
                                            // For previous leads: previous participant's respondedAt - this participant's respondedAt
                                            const endTime = actualIndex === 0
                                                ? new Date()
                                                : new Date(participants[actualIndex - 1].respondedAt!);
                                            const durationMs = Math.max(0, endTime.getTime() - acceptedAt.getTime()); // Ensure non-negative
                                            const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
                                            const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
                                            const durationStr = durationHours > 0
                                                ? `${durationHours}h ${durationMinutes}m`
                                                : `${durationMinutes}m`;

                                            return (
                                                <TableRow key={assignment.id}>
                                                    <TableCell>
                                                        {assignment.user.firstName} {assignment.user.lastName}
                                                    </TableCell>
                                                    <TableCell>
                                                        <Tooltip content={<span>{assignment.user.emailAddress}</span>} relationship="label">
                                                            <span style={{
                                                                display: 'block',
                                                                maxWidth: '200px',
                                                                overflow: 'hidden',
                                                                textOverflow: 'ellipsis',
                                                                whiteSpace: 'nowrap',
                                                                cursor: 'help'
                                                            }}>
                                                                {assignment.user.emailAddress}
                                                            </span>
                                                        </Tooltip>
                                                    </TableCell>
                                                    <TableCell>
                                                        {designatedAt ? format(designatedAt, 'MMM dd, yyyy HH:mm') : '-'}
                                                    </TableCell>
                                                    <TableCell>
                                                        {format(acceptedAt, 'MMM dd, yyyy HH:mm')}
                                                    </TableCell>
                                                    <TableCell>{responseTimeStr}</TableCell>
                                                    <TableCell>{durationStr}</TableCell>
                                                </TableRow>
                                            );
                                        })}
                                    </TableBody>
                                </Table>
                                {totalParticipantsPages > 1 && (
                                    <div className="paginationContainer" style={{marginTop: '16px'}}>
                                        <div className="pageInfo">
                                            Showing {participantsPage * participantsPageSize + 1} - {Math.min((participantsPage + 1) * participantsPageSize, participants.length)} of {participants.length}
                                        </div>
                                        <div className="paginationControls">
                                            <Button
                                                size="small"
                                                onClick={() => setParticipantsPage(0)}
                                                disabled={participantsPage === 0}
                                            >
                                                First
                                            </Button>
                                            <Button
                                                size="small"
                                                onClick={() => setParticipantsPage(prev => Math.max(0, prev - 1))}
                                                disabled={participantsPage === 0}
                                            >
                                                Previous
                                            </Button>
                                            <span className="pageInfo">
                                                Page {participantsPage + 1} of {totalParticipantsPages}
                                            </span>
                                            <Button
                                                size="small"
                                                onClick={() => setParticipantsPage(prev => Math.min(totalParticipantsPages - 1, prev + 1))}
                                                disabled={participantsPage >= totalParticipantsPages - 1}
                                            >
                                                Next
                                            </Button>
                                            <Button
                                                size="small"
                                                onClick={() => setParticipantsPage(totalParticipantsPages - 1)}
                                                disabled={participantsPage >= totalParticipantsPages - 1}
                                            >
                                                Last
                                            </Button>
                                        </div>
                                    </div>
                                )}
                            </DialogContent>
                            <DialogActions>
                                <Button appearance="secondary" onClick={() => setParticipantsDialogOpen(false)}>
                                    Close
                                </Button>
                            </DialogActions>
                        </DialogBody>
                    </DialogSurface>
                </Dialog>
            </Card>
        </>
    );
};
