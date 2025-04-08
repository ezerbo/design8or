import React, {useState} from "react";
import {Footer} from "../Footer/Footer";
import {Header} from "../Header/Header";
import {PoolStats} from "../Pools/PoolStats/PoolStats";
import {Users} from "../Users/Users";
import {Button, Card, Divider} from "@fluentui/react-components";
import styles from './Home.module.css';
import {FolderPeopleRegular, PeopleTeamRegular, SettingsCogMultipleRegular} from "@fluentui/react-icons";
import {Parameters} from "../Parameters/Parameters";
import {Candidates} from "../Candidates/Candidates"; // Assuming you have CSS module for styling


export const Home: React.FunctionComponent = () => {

    const [isUsersSectionVisible, showUsersSection] = useState<boolean>(true);
    const [isCandidatesSectionVisible, showCandidatesSection] = useState<boolean>(false);
    const [isParametersSectionVisible, showParametersSection] = useState<boolean>(false);

    const toggleUsersSection = () => {
        showUsersSection(true);
        showCandidatesSection(false);
        showParametersSection(false);
    }

    const toggleCandidatesSection = () => {
        showCandidatesSection(true);
        showUsersSection(false);
        showParametersSection(false);
    }

    const toggleParametersSection = () => {
        showParametersSection(true);
        showUsersSection(false);
        showCandidatesSection(false);
    }

    return (
        <div>
            <Card className={styles.card}>
                <Header/>
                <div className={styles.horizontalLayout}>
                    <PoolStats/>
                    <div>
                        {isUsersSectionVisible && <Users/>}
                        {isCandidatesSectionVisible && <Candidates/>}
                        {isParametersSectionVisible && <Parameters/>}

                        <div className={styles.controls}>
                            <Divider/>
                            <div className={styles.controlButtons}>
                                <Button shape="circular" className={styles.controlButton} icon={<PeopleTeamRegular/>}
                                        onClick={toggleUsersSection}>Users</Button>
                                <Button shape="circular" className={styles.controlButton} icon={<FolderPeopleRegular/>}
                                        onClick={toggleCandidatesSection}>Candidates</Button>
                                <Button shape="circular" className={styles.controlButton} icon={<SettingsCogMultipleRegular/>}
                                        onClick={toggleParametersSection}>Parameters</Button>
                            </div>
                        </div>
                    </div>
                </div>
                <Footer/>
            </Card>
        </div>
    );

};