import React from "react";
import {Footer} from "../Footer/Footer";
import {Header} from "../Header/Header";
import {Card} from "@fluentui/react-components";
import {Nav} from "../Nav/Nav";
import {Pools} from "./Pools";
import styles from './PoolsPage.module.css';

export const PoolsPage: React.FunctionComponent = () => {
    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.mainLayout}>
                    <Nav />
                    <div className={styles.content}>
                        <Pools />
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );
};
