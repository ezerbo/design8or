import * as React from 'react';
import {Divider, ToolbarProps} from "@fluentui/react-components";
import {PeopleCommunityRegular, PeopleSyncRegular} from "@fluentui/react-icons";
import './Header.css';

export const Header = (props: Partial<ToolbarProps>) => {
    return (
        <div className="header">
            <div className="headerContent">
                <div className="brandingSection">
                    <div className="logoTitleGroup">
                        <div className="logoWrapper">
                            <PeopleSyncRegular className="logo" />
                        </div>
                        <div className="titleGroup">
                            <div className="title">Desig8or</div>
                            <div className="tagline">Smart Lead Rotation Management</div>
                        </div>
                    </div>
                </div>
                <div className="descriptionSection">
                    <div className="featuresList">
                        <div className="feature">
                            <span className="featureIcon">🎯</span>
                            <span className="featureText">Automated Rotation</span>
                        </div>
                        <div className="feature">
                            <span className="featureIcon">🔔</span>
                            <span className="featureText">Real-time Notifications</span>
                        </div>
                        <div className="feature">
                            <span className="featureIcon">📊</span>
                            <span className="featureText">Track History</span>
                        </div>
                        <div className="feature">
                            <span className="featureIcon">⚖️</span>
                            <span className="featureText">Fair Distribution</span>
                        </div>
                    </div>
                </div>
            </div>
            <Divider />
        </div>
    );
};