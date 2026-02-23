import React, {useEffect, useMemo} from "react";
import {Pool} from "../Pool";
import {format} from "date-fns";
import {ClockRegular, StatusRegular, PeopleRegular, AddCircleRegular, DeleteRegular} from "@fluentui/react-icons";
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
    Dialog,
    DialogSurface,
    DialogTitle,
    DialogBody,
    DialogContent,
    DialogActions,
} from "@fluentui/react-components";
import './PoolList.css';


const useStyles = makeStyles({
    card: {
        padding: '16px',
        boxShadow: tokens.shadow8,
        backgroundColor: tokens.colorNeutralBackground1,
        borderRadius: tokens.borderRadiusMedium,
        flex: 1,
    },
    title: {
        fontSize: '18px',
        fontWeight: 300,
        marginBottom: '16px',
        color: '#0078d4',
    },
    divider: {
        marginBottom: '16px',
    },
});

const columns = [
    {columnKey: "startDate", label: "Start Date", icon: <ClockRegular/>},
    {columnKey: "endDate", label: "End Date", icon: <ClockRegular/>},
    {columnKey: "status", label: "Status", icon: <StatusRegular/>},
    {columnKey: "participantCount", label: "Participants", icon: <PeopleRegular/>}
];

const dateFormat = 'yyyy-MM-dd';
const DEFAULT_PAGE_SIZE = 10;

const getItems = (pools: Pool[]) => {
    return pools.map(p => {
        return {
            id: p.id,
            startDate: {label: format(new Date(p.startDate), dateFormat)},
            endDate: {label: p.endDate !== null ? format(new Date(p.endDate), dateFormat) : '-'},
            status: {label: p.endDate === null ? 'ACTIVE' : 'ENDED'},
            participantCount: {label: (p.participantCount ?? 0).toString()},
            isEnded: p.endDate !== null
        }
    });
}

export const PoolList: React.FunctionComponent<{
    pools: Pool[];
    onStartNewPool: () => void;
    onDeletePools: (poolIds: number[]) => void;
    loading: boolean;
}> = ({pools, onStartNewPool, onDeletePools, loading}) => {
    const styles = useStyles();

    const [allItems, setAllItems] = React.useState<any[]>([]);
    const [currentPage, setCurrentPage] = React.useState(0);
    const [pageSize] = React.useState(DEFAULT_PAGE_SIZE);
    const [selectedRows, setSelectedRows] = React.useState<Set<number>>(new Set());
    const [deleteDialogOpen, setDeleteDialogOpen] = React.useState(false);
    const [poolsToDelete, setPoolsToDelete] = React.useState<number[]>([]);

    useEffect(() => {
        setAllItems(getItems(pools));
    }, [pools]);

    const paginatedItems = useMemo(() => {
        const startIndex = currentPage * pageSize;
        const endIndex = startIndex + pageSize;
        return allItems.slice(startIndex, endIndex);
    }, [allItems, currentPage, pageSize]);

    const totalPages = Math.ceil(allItems.length / pageSize);

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
    const endItem = Math.min((currentPage + 1) * pageSize, allItems.length);

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
        if (selectedRows.size === paginatedItems.length) {
            setSelectedRows(new Set());
        } else {
            setSelectedRows(new Set(paginatedItems.map((_, i) => i)));
        }
    };

    const allRowsSelected = selectedRows.size === paginatedItems.length && paginatedItems.length > 0;
    const someRowsSelected = selectedRows.size > 0 && selectedRows.size < paginatedItems.length;

    const handleDelete = () => {
        const endedPoolIds: number[] = [];
        selectedRows.forEach(index => {
            const item = paginatedItems[index];
            if (item.isEnded) {
                endedPoolIds.push(item.id);
            }
        });

        if (endedPoolIds.length > 0) {
            setPoolsToDelete(endedPoolIds);
            setDeleteDialogOpen(true);
        }
    };

    const confirmDelete = () => {
        onDeletePools(poolsToDelete);
        setSelectedRows(new Set()); // Clear selection after delete
        setDeleteDialogOpen(false);
        setPoolsToDelete([]);
    };

    // Check if any selected pools are ended (can be deleted) and none are active
    const hasEndedPoolsSelected = Array.from(selectedRows).some(index => {
        const item = paginatedItems[index];
        return item.isEnded;
    });

    const hasActivePoolSelected = Array.from(selectedRows).some(index => {
        const item = paginatedItems[index];
        return !item.isEnded;
    });

    return (
        <Card className={styles.card}>
            <div className={styles.title}>Pool History</div>
            <Divider className={styles.divider} />
            <div style={{display: 'flex', justifyContent: 'flex-end', gap: '8px', marginBottom: '16px'}}>
                <Button
                    appearance="secondary"
                    icon={<DeleteRegular />}
                    onClick={handleDelete}
                    disabled={loading || !hasEndedPoolsSelected || hasActivePoolSelected}
                    style={{
                        backgroundColor: (loading || !hasEndedPoolsSelected || hasActivePoolSelected) ? undefined : '#d13438',
                        color: (loading || !hasEndedPoolsSelected || hasActivePoolSelected) ? undefined : 'white',
                        borderColor: (loading || !hasEndedPoolsSelected || hasActivePoolSelected) ? undefined : '#d13438'
                    }}
                >
                    Delete
                </Button>
                <Button
                    appearance="primary"
                    icon={<AddCircleRegular />}
                    onClick={onStartNewPool}
                    disabled={loading}
                >
                    Start New Pool
                </Button>
            </div>
            <Table arial-label="Pools table">
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
                    {paginatedItems.map((item, i) => (
                        <TableRow key={i}>
                            <TableSelectionCell
                                checked={selectedRows.has(i)}
                                onClick={() => toggleRow(i)}
                            />
                            <TableCell>
                                <TableCellLayout media={item.startDate.icon}>
                                    {item.startDate.label}
                                </TableCellLayout>
                            </TableCell>
                            <TableCell>
                                <TableCellLayout media={item.endDate.icon}>
                                    {item.endDate.label}
                                </TableCellLayout>
                            </TableCell>
                            <TableCell>
                                <TableCellLayout media={item.status.icon}>
                                    <span className={item.status.label === 'ENDED' ? 'statusEnded' : 'statusActive'}>
                                        {item.status.label}
                                    </span>
                                </TableCellLayout>
                            </TableCell>
                            <TableCell>
                                <TableCellLayout media={item.participantCount.icon}>
                                    {item.participantCount.label}
                                </TableCellLayout>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
            {totalPages > 1 && (
                <div className="paginationContainer">
                    <div className="pageInfo">
                        Showing {startItem} - {endItem} of {allItems.length}
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

            <Dialog open={deleteDialogOpen} onOpenChange={(_, data) => setDeleteDialogOpen(data.open)}>
                <DialogSurface>
                    <DialogBody>
                        <DialogTitle>Confirm Deletion</DialogTitle>
                        <DialogContent>
                            Are you sure you want to delete {poolsToDelete.length} pool{poolsToDelete.length > 1 ? 's' : ''}?
                            This action cannot be undone.
                        </DialogContent>
                        <DialogActions>
                            <Button appearance="secondary" onClick={() => setDeleteDialogOpen(false)}>
                                Cancel
                            </Button>
                            <Button
                                appearance="primary"
                                onClick={confirmDelete}
                                style={{
                                    backgroundColor: '#d13438',
                                    color: 'white',
                                    borderColor: '#d13438'
                                }}
                            >
                                Delete
                            </Button>
                        </DialogActions>
                    </DialogBody>
                </DialogSurface>
            </Dialog>
        </Card>
    );
}