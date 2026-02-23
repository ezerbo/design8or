import React from "react";
import {Footer} from "../Footer/Footer";
import {Header} from "../Header/Header";
import {Card} from "@fluentui/react-components";
import {Nav} from "../Nav/Nav";
import {Users} from "./Users";
import styles from './UsersPage.module.css';

export const UsersPage: React.FunctionComponent = () => {
    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.mainLayout}>
                    <Nav />
                    <div className={styles.content}>
                        <Users />
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );
};
