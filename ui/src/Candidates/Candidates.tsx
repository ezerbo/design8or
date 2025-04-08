import React from "react";
import {Card, Divider} from "@fluentui/react-components";
import {PeopleCommunity48Regular} from "@fluentui/react-icons";

import styles from './Candidates.module.css';

export const Candidates: React.FunctionComponent = () => {
    return (
        <div>
            <Card className={styles.card}>
                <Divider><PeopleCommunity48Regular /></Divider>
            </Card>
        </div>
    )
}