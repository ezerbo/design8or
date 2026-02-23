import React from "react";
import {Card} from "@fluentui/react-components";
import {Header} from "../Header/Header";
import {Nav} from "../Nav/Nav";
import {Footer} from "../Footer/Footer";
import {Configurations} from "./Configurations";
import styles from '../Home/Home.module.css';

export const ConfigurationsPage: React.FunctionComponent = () => {
    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.mainLayout}>
                    <Nav/>
                    <div className={styles.content}>
                        <Configurations/>
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );
};
