import React, {useEffect} from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import './Design8or.css';
import {Home} from './Home/Home';
import {PoolsPage} from "./Pools/PoolsPage";
import {UsersPage} from "./Users/UsersPage";
import {DesignationResponse} from "./Designation/DesignationResponse";
import {ConfigurationsPage} from "./Configurations/ConfigurationsPage";
import {SubscriptionsPage} from "./Subscriptions/SubscriptionsPage";
import {NotificationService} from "./Commons/NotificationService";

export const Design8or: React.FunctionComponent = () => {
    useEffect(() => {
        // Request notification permissions and subscribe when app loads
        const initializeNotifications = async () => {
            try {
                // Check if already subscribed
                const isSubscribed = await NotificationService.isSubscribed();

                if (!isSubscribed) {
                    // Only request if not already subscribed
                    console.log('Requesting notification permissions...');
                    await NotificationService.subscribe();
                }
            } catch (error) {
                console.error('Error initializing notifications:', error);
            }
        };

        initializeNotifications();
    }, []);

    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<Home />} />
                <Route path='/users' element={<UsersPage />} />
                <Route path='/pools' element={<PoolsPage />} />
                <Route path='/configurations' element={<ConfigurationsPage />} />
                <Route path='/subscriptions' element={<SubscriptionsPage />} />
                <Route path='/designation-response' element={<DesignationResponse />} />
                {/*<Route path='/designations' element={<Designations />} />*/}
            </Routes>
        </BrowserRouter>
    );
};
