import React from "react";
import {
    Avatar,
    Card, createTableColumn,
    DataGrid,
    DataGridBody,
    DataGridCell,
    DataGridHeader,
    DataGridHeaderCell,
    DataGridProps,
    DataGridRow,
    Divider,
    makeStyles,
    PresenceBadgeStatus,
    TableCellLayout,
    TableColumnDefinition
} from "@fluentui/react-components";
import {
    DocumentPdfRegular,
    DocumentRegular,
    EditRegular,
    FolderRegular,
    OpenRegular,
    PeopleCommunity48Regular,
    PeopleRegular,
    VideoRegular
} from "@fluentui/react-icons";

const useStyles = makeStyles({
    card: {
        margin: "auto",
        width: "1200px",
        maxWidth: "100%",
    },
});

type FileCell = {
    label: string;
    icon: JSX.Element;
};

type LastUpdatedCell = {
    label: string;
    timestamp: number;
};

type LastUpdateCell = {
    label: string;
    icon: JSX.Element;
};

type AuthorCell = {
    label: string;
    status: PresenceBadgeStatus;
};

type Item = {
    file: FileCell;
    author: AuthorCell;
    lastUpdated: LastUpdatedCell;
    lastUpdate: LastUpdateCell;
};


const items: Item[] = [
    {
        file: { label: "Meeting notes", icon: <DocumentRegular /> },
        author: { label: "Max Mustermann", status: "available" },
        lastUpdated: { label: "7h ago", timestamp: 1 },
        lastUpdate: {
            label: "You edited this",
            icon: <EditRegular />,
        },
    },
    {
        file: { label: "Thursday presentation", icon: <FolderRegular /> },
        author: { label: "Erika Mustermann", status: "busy" },
        lastUpdated: { label: "Yesterday at 1:45 PM", timestamp: 2 },
        lastUpdate: {
            label: "You recently opened this",
            icon: <OpenRegular />,
        },
    },
    {
        file: { label: "Training recording", icon: <VideoRegular /> },
        author: { label: "John Doe", status: "away" },
        lastUpdated: { label: "Yesterday at 1:45 PM", timestamp: 2 },
        lastUpdate: {
            label: "You recently opened this",
            icon: <OpenRegular />,
        },
    },
    {
        file: { label: "Purchase order", icon: <DocumentPdfRegular /> },
        author: { label: "Jane Doe", status: "offline" },
        lastUpdated: { label: "Tue at 9:30 AM", timestamp: 3 },
        lastUpdate: {
            label: "You shared this in a Teams chat",
            icon: <PeopleRegular />,
        },
    },
    {
        file: { label: "Purchase order", icon: <DocumentPdfRegular /> },
        author: { label: "Jane Doe", status: "offline" },
        lastUpdated: { label: "Tue at 9:30 AM", timestamp: 3 },
        lastUpdate: {
            label: "You shared this in a Teams chat",
            icon: <PeopleRegular />,
        },
    },
    {
        file: { label: "Purchase order", icon: <DocumentPdfRegular /> },
        author: { label: "Jane Doe", status: "offline" },
        lastUpdated: { label: "Tue at 9:30 AM", timestamp: 3 },
        lastUpdate: {
            label: "You shared this in a Teams chat",
            icon: <PeopleRegular />,
        },
    },
    {
        file: { label: "Purchase order", icon: <DocumentPdfRegular /> },
        author: { label: "Jane Doe", status: "offline" },
        lastUpdated: { label: "Tue at 9:30 AM", timestamp: 3 },
        lastUpdate: {
            label: "You shared this in a Teams chat",
            icon: <PeopleRegular />,
        },
    },
    {
        file: { label: "Purchase order", icon: <DocumentPdfRegular /> },
        author: { label: "Jane Doe", status: "offline" },
        lastUpdated: { label: "Tue at 9:30 AM", timestamp: 3 },
        lastUpdate: {
            label: "You shared this in a Teams chat",
            icon: <PeopleRegular />,
        },
    },
    {
        file: { label: "Purchase order", icon: <DocumentPdfRegular /> },
        author: { label: "Jane Doe", status: "offline" },
        lastUpdated: { label: "Tue at 9:30 AM", timestamp: 3 },
        lastUpdate: {
            label: "You shared this in a Teams chat",
            icon: <PeopleRegular />,
        },
    }
];

const columns: TableColumnDefinition<Item>[] = [
    createTableColumn<Item>({
        columnId: "file",
        compare: (a, b) => {
            return a.file.label.localeCompare(b.file.label);
        },
        renderHeaderCell: () => {
            return "File";
        },
        renderCell: (item) => {
            return (
                <TableCellLayout media={item.file.icon}>
                    {item.file.label}
                </TableCellLayout>
            );
        },
    }),
    createTableColumn<Item>({
        columnId: "author",
        compare: (a, b) => {
            return a.author.label.localeCompare(b.author.label);
        },
        renderHeaderCell: () => {
            return "Author";
        },
        renderCell: (item) => {
            return (
                <TableCellLayout
                    media={
                        <Avatar
                            aria-label={item.author.label}
                            name={item.author.label}
                            badge={{ status: item.author.status }}
                        />
                    }
                >
                    {item.author.label}
                </TableCellLayout>
            );
        },
    }),
    createTableColumn<Item>({
        columnId: "lastUpdated",
        compare: (a, b) => {
            return a.lastUpdated.timestamp - b.lastUpdated.timestamp;
        },
        renderHeaderCell: () => {
            return "Last updated";
        },

        renderCell: (item) => {
            return item.lastUpdated.label;
        },
    }),
    createTableColumn<Item>({
        columnId: "lastUpdate",
        renderHeaderCell: () => {
            return "Not sortable";
        },
        renderCell: (item) => {
            return (
                <TableCellLayout media={item.lastUpdate.icon}>
                    {item.lastUpdate.label}
                </TableCellLayout>
            );
        },
    }),
];

export const Users: React.FunctionComponent = () => {
    const styles = useStyles();
    const defaultSortState = React.useMemo<Parameters<NonNullable<DataGridProps["onSortChange"]>>[1]
    >(() => ({ sortColumn: "file", sortDirection: "ascending" }), []);
    return (
        <div>
            <Card className={styles.card}>
               <Divider><PeopleCommunity48Regular /></Divider>
                <DataGrid
                    items={items}
                    columns={columns}
                    defaultSortState={defaultSortState}
                    style={{ minWidth: "500px" }}>
                    <DataGridHeader>
                        <DataGridRow>
                            {({ renderHeaderCell }) => (
                                <DataGridHeaderCell>{renderHeaderCell()}</DataGridHeaderCell>
                            )}
                        </DataGridRow>
                    </DataGridHeader>
                    <DataGridBody<Item>>
                        {({ item, rowId }) => (
                            <DataGridRow<Item> key={rowId}>
                                {({ renderCell }) => (
                                    <DataGridCell>{renderCell(item)}</DataGridCell>
                                )}
                            </DataGridRow>
                        )}
                    </DataGridBody>
                </DataGrid>
            </Card>
        </div>
    );
};