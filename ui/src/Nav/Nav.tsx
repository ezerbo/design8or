import React from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { HomeRegular, PeopleRegular, SettingsRegular, GridRegular, PlugDisconnectedRegular } from "@fluentui/react-icons";
import { Card } from "@fluentui/react-components";
import './Nav.css';

export const Nav: React.FunctionComponent = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const menuItems = [
        { path: '/', label: 'Home', icon: <HomeRegular /> },
        { path: '/users', label: 'Users', icon: <PeopleRegular /> },
        { path: '/pools', label: 'Pools', icon: <GridRegular /> },
        { path: '/configurations', label: 'Configurations', icon: <SettingsRegular /> },
        { path: '/subscriptions', label: 'Subscriptions', icon: <PlugDisconnectedRegular /> }
    ];

    return (
        <Card className="navCard">
            <nav className="nav">
                <ul className="navList">
                    {menuItems.map((item) => (
                        <li
                            key={item.path}
                            className={`navItem ${location.pathname === item.path ? 'active' : ''}`}
                            onClick={() => navigate(item.path)}
                        >
                            <span className="navIcon">{item.icon}</span>
                            <span className="navLabel">{item.label}</span>
                        </li>
                    ))}
                </ul>
            </nav>
        </Card>
    );
};
