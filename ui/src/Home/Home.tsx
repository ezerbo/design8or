import React from "react";
import {Footer} from "../Footer/Footer";
import {Header} from "../Header/Header";
import {Users} from "../Users/Users";
import {Button, Card, Divider} from "@fluentui/react-components";
import styles from './Home.module.css';
import {PeopleAddRegular} from "@fluentui/react-icons";
import {CurrentPool} from "../CurrentPool/CurrentPool";

export const Home: React.FunctionComponent = () => {

    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.horizontalLayout}>
                    <CurrentPool />
                    <div>
                        <Users/>
                        <div className={styles.controls}>
                            <Divider/>
                            <div className={styles.controlButtons}>
                                <Button shape="circular"
                                        className={styles.controlButton}
                                        icon={<PeopleAddRegular/>}>
                                    Add
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );

};