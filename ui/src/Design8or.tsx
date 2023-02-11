import React from 'react';
import {BrowserRouter, Routes, Route} from "react-router-dom";
import './Design8or.css';
import {Home} from './Home/Home';
import {Parameters} from "./Parameters/Parameters";
import {Users} from "./Users/Users";
import {Pools} from "./Pools/Pools";
import {Designations} from "./Designations/Designations";

export const Design8or: React.FunctionComponent = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path='/' element={<Home />} />
                <Route path='/parameters' element={<Parameters />} />
                <Route path='/users' element={<Users />} />
                <Route path='/pools' element={<Pools />} />
                <Route path='/designations' element={<Designations />} />
            </Routes>
        </BrowserRouter>
    );
};
