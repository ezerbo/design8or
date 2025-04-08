import React from "react";
import {Card, Divider} from "@fluentui/react-components";
import styles from "../Parameters/Parameters.module.css";
import {SettingsCogMultiple24Regular} from "@fluentui/react-icons";

export const Parameters: React.FunctionComponent = () => {

    return (
        <div>
            <div>
                <Card className={styles.card}>
                    <Divider><SettingsCogMultiple24Regular /></Divider>
                </Card>
            </div>
        </div>
    );
};