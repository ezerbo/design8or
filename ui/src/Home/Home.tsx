import React from "react";
import {Footer} from "../Footer/Footer";
import {Header} from "../Header/Header";
import {Card} from "@fluentui/react-components";
import styles from './Home.module.css';
import {Candidates} from "./Candidates";
import {Nav} from "../Nav/Nav";

export const Home: React.FunctionComponent = () => {

    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.mainLayout}>
                    <Nav />
                    <div className={styles.content}>
                        <Candidates />
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );

};