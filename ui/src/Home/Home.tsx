import React from "react";
import { Footer } from "../Footer/Footer";
import { Header } from "../Header/Header";
import {UserStats} from "../Users/UserStats/UserStats";
import {Stack} from "@fluentui/react";
import {PoolStats} from "../Pools/PoolStats/PoolStats";

export const Home: React.FunctionComponent = () => {

    return (
        <div>
            <Header />
            <Stack horizontal>
                <Stack.Item>
                    <PoolStats />
                </Stack.Item>
                <Stack.Item>
                    <UserStats />
                </Stack.Item>
            </Stack>
            <Footer />
        </div>
    );
};