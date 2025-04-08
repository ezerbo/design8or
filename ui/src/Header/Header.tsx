import * as React from 'react';
import {Divider, ToolbarProps} from "@fluentui/react-components";
import {PersonClockRegular} from "@fluentui/react-icons";


export const Header = (props: Partial<ToolbarProps>) => {
    return (
        <div>
            <Divider><PersonClockRegular style={{ fontSize: '100px', marginRight: '10px' }} /></Divider>
        </div>
    );
};