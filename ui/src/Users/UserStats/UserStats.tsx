import React from "react";
import axios from "axios";
import {api_base_url, handleErrors} from "../../commons/http.util";
import {User} from "../User";


interface UserStatsState {
    users: User[]
}

export class UserStats extends React.Component<{}, UserStatsState> {

    constructor(props: any) {
        super(props);
        this.state = {users: []};
    }

    componentDidMount() {
        this.getUsers();
    }

    render() {
        const {users} = this.state;
        return (
           <div>
               Users: {users.length}
           </div>
        );
    }

    private getUsers = () => {
        axios({
            url: `${api_base_url}/users`,
            method: 'GET',
            headers: {'Accept': 'application/json'}
        })
            .then(res => handleErrors(res))
            .then(res => {
                this.setState(() => {
                    return { users: res.data };
                })
            });
    }
}