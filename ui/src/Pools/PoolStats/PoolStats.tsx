import React, {useEffect, useState} from "react";
import style from "./PoolStats.module.css"
import {Card, Divider, makeStyles, ProgressBar, Spinner, Tooltip} from "@fluentui/react-components";
import {PeopleAudience32Regular, PersonSupport32Regular, SlideTextPerson32Regular} from "@fluentui/react-icons";
import axios from "axios";
import {api_base_url, handleErrors} from "../../commons/http.util";
import {CurrentPoolStats} from "./CurrentPoolStats";
import {Designation} from "../../Designations/Designation";

const useStyles = makeStyles({
    card: {
        margin: "10px; auto",
        width: "440px",
        maxWidth: "100%",
    },
    progressBar: {
        height: "20px"
    }
});

export const PoolStats: React.FunctionComponent = () => {

    const [stats, setStats] = useState<CurrentPoolStats>();
    const [designation, setDesignation] = useState<Designation>();

    const getStats = () => {
        axios({
            url: `${api_base_url}/pools/stats`,
            method: 'GET',
            headers: {'Accept': 'application/json'}
        })
            .then(res => handleErrors(res))
            .then(res => {
                setStats(res.data);
            });
    }

    const getCurrentDesignation = () => {
        axios({
            url: `${api_base_url}/designations/current`,
            method: 'GET',
            headers: {'Accept': 'application/json'}
        })
            .then(res => handleErrors(res))
            .then(res => {
                setDesignation(res.data);
            });
    }


    useEffect(() => {
        getStats();
        getCurrentDesignation();
    }, []);

    const styles = useStyles();
    return (
        <div>
            <Card className={styles.card}>
                <Tooltip content="Lead" relationship="label">
                    <PersonSupport32Regular/>
                </Tooltip>
                <Divider/>
                {
                    stats ? (
                        <ul>
                            <li className={style.listItem}>Firstname: {stats.pool?.lead?.firstName}</li>
                            <li>Lastname: {stats.pool?.lead?.lastName}</li>
                            <li>Email Address: {stats.pool?.lead?.emailAddress}</li>
                        </ul>
                    ) : (<Spinner size="small"/>)
                }
                <Divider/>
                Designation Countdown: ...
            </Card>

            <Card className={styles.card}>
                <Tooltip content="Designated" relationship="label">
                    <SlideTextPerson32Regular/>
                </Tooltip>
                <Divider/>
                {
                    designation ? (
                        <ul>
                            <li className={style.listItem}>Firstname: {designation.user?.firstName}</li>
                            <li>Lastname: {designation.user?.lastName}</li>
                            <li>Email Address: {designation.user?.emailAddress}</li>
                        </ul>
                    ) : (<Spinner size="small"/>)
                }
                <Divider/>
                {designation && <span>Status: {designation.status}</span>}
            </Card>


            <Card className={styles.card}>
                <Tooltip content="Pools" relationship="label">
                    <PeopleAudience32Regular/>
                </Tooltip>
                <Divider/>
                <ProgressBar className={styles.progressBar} value={0.5}/>
                <Divider/>
                Current Pool's Status: ...
            </Card>
        </div>
    );
}