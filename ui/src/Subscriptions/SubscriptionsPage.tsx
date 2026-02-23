import React from "react";
import {Footer} from "../Footer/Footer";
import {Header} from "../Header/Header";
import {Card} from "@fluentui/react-components";
import styles from '../Home/Home.module.css';
import {Subscriptions} from "./Subscriptions";
import {Nav} from "../Nav/Nav";

export const SubscriptionsPage: React.FunctionComponent = () => {
    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.mainLayout}>
                    <Nav />
                    <div className={styles.content}>
                        <Subscriptions />
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );
};
