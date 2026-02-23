import React, {useEffect} from "react";
import {PoolList} from "./PoolList/PoolList";
import {httpGet} from "../Commons/Http.util";
import {Pool} from "./Pool";
import {GET_POOLS_URL, START_NEW_POOL_URL, deletePoolPath} from "../Commons/Paths";
import {PendingDesignation} from "./Designation/PendingDesignation";
import {Toast, ToastTitle, Toaster, useToastController, useId} from "@fluentui/react-components";
import axios from "axios";
import './Pools.css';


export const Pools: React.FunctionComponent = () => {
    const toasterId = useId("toaster");
    const {dispatchToast} = useToastController(toasterId);

    const [pools, setPools] = React.useState<Pool[]>();
    const [, setCurrentPool] = React.useState<Pool>();
    const [loading, setLoading] = React.useState(false);
    const [refreshKey, setRefreshKey] = React.useState(0);

    const fetchPools = async () => {
        try {
            const pools = await httpGet<Pool[]>(GET_POOLS_URL);
            setPools(pools);
            setCurrentPool(pools.find(pool => pool.endDate === null));
        } catch (error) {
            console.error('Error fetching pools:', error);
        }
    };

    useEffect(() => {
        fetchPools();
    }, []);

    const handleStartNewPool = async () => {
        setLoading(true);
        try {
            await axios.post(START_NEW_POOL_URL);
            await fetchPools(); // Refresh the pools list
            setRefreshKey(prev => prev + 1); // Force PendingDesignation to refresh
            dispatchToast(
                <Toast>
                    <ToastTitle>New pool started successfully</ToastTitle>
                </Toast>,
                {intent: 'success'}
            );
        } catch (error) {
            console.error('Error starting new pool:', error);
            dispatchToast(
                <Toast>
                    <ToastTitle>Error starting new pool</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        } finally {
            setLoading(false);
        }
    };

    const handleDeletePools = async (poolIds: number[]) => {
        setLoading(true);
        try {
            await Promise.all(poolIds.map(id => axios.delete(deletePoolPath(id))));
            await fetchPools(); // Refresh the pools list
            setRefreshKey(prev => prev + 1); // Force PendingDesignation to refresh
            dispatchToast(
                <Toast>
                    <ToastTitle>Pool(s) deleted successfully</ToastTitle>
                </Toast>,
                {intent: 'success'}
            );
        } catch (error: any) {
            console.error('Error deleting pools:', error);
            const errorMessage = error.response?.data?.message || 'Error deleting pool(s)';
            dispatchToast(
                <Toast>
                    <ToastTitle>{errorMessage}</ToastTitle>
                </Toast>,
                {intent: 'error'}
            );
        } finally {
            setLoading(false);
        }
    };
    return (
        <div className="container">
            <Toaster toasterId={toasterId} position="top-end" />
            <div className="poolsRow">
                <PendingDesignation key={refreshKey} />
                {pools && <PoolList pools={pools} onStartNewPool={handleStartNewPool} onDeletePools={handleDeletePools} loading={loading} />}
            </div>
            {/*<div className="usersRow">*/}
            {/*    <div className="section">*/}
            {/*        {currentPool && (*/}
            {/*            <Users apiUrl={`http://localhost:8080/pools/${currentPool.id}/candidates`} />*/}
            {/*        )}*/}
            {/*    </div>*/}
            {/*    <div className={styles.section}>*/}
            {/*        {currentPool && (*/}
            {/*            <Users apiUrl={`http://localhost:8080/pools/${currentPool.id}/participants`} />*/}
            {/*        )}*/}
            {/*    </div>*/}
            {/*</div>*/}
        </div>
    );
};