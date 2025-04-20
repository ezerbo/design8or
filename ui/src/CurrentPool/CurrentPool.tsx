import React, {useEffect, useState} from "react";
import style from "./CurrentPool.module.css"
import {Card, Divider, makeStyles, ProgressBar, Spinner, Tooltip} from "@fluentui/react-components";
import {PeopleAudience32Regular, PersonSupport32Regular, SlideTextPerson32Regular} from "@fluentui/react-icons";
import {httpGet} from "../Commons/Http.util";
import {CurrentPoolStats} from "./CurrentPoolStats";
import {Designation} from "../Designations/Designation";
import {CURRENT_DESIGNATIONS_URL, POOL_STATS_URL} from "../Commons/Paths";

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

export const CurrentPool: React.FunctionComponent = () => {

    const [stats, setStats] = useState<CurrentPoolStats>();
    const [designation, setDesignation] = useState<Designation>();

    useEffect(() => {
        httpGet<CurrentPoolStats>(POOL_STATS_URL)
            .then((stats) => { setStats(stats); });

        httpGet<Designation>(CURRENT_DESIGNATIONS_URL)
            .then(designation => setDesignation(designation));
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