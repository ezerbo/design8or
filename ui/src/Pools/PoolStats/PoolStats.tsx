import React from "react";
import axios from "axios";
import {api_base_url, handleErrors, toDate} from "../../commons/http.util";
import {PStats} from "./PStats";

interface PoolStatsState {
    stats: PStats
}

export class PoolStats extends React.Component<{}, PoolStatsState> {

    constructor(props: any) {
        super(props);
        this.state = {stats: {}};
    }

    componentDidMount() {
        this.getStats();
    }

    render() {
        const { stats } = this.state;
        return (
            <div>
                <span>
                    # of pools: {stats.count} <br/>
                </span>
                <span>
                    Current Pools Start Date: {toDate(stats.currentPool?.startDate)} <br/>
                </span>
                <span>
                    - firstName: {stats.currentPool?.lead?.firstName} <br/>
                </span>
                <span>
                    - LastName : {stats.currentPool?.lead?.lastName}<br/>
                </span>
                <span>
                    - emailAddress: {stats.currentPool?.lead?.emailAddress} <br/>
                </span>
            </div>
        );
    }

    private getStats = () => {
        axios({
            url: `${api_base_url}/pools/stats`,
            method: 'GET',
            headers: {'Accept': 'application/json'}
        })
            .then(res => handleErrors(res))
            .then(res => {
                this.setState(() => {
                    return { stats: res.data };
                })
            });
    }
}