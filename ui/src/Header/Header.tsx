import * as React from 'react';
import {CommandBar, ICommandBarItemProps} from '@fluentui/react/lib/CommandBar';
import {FocusTrapZone} from '@fluentui/react/lib/FocusTrapZone';
import {initializeIcons} from '@fluentui/react';

initializeIcons();

export const Header: React.FunctionComponent = () => {

    const [enableFocusTrap] = React.useState(false);

    return (
        <FocusTrapZone disabled={!enableFocusTrap}>
            <CommandBar
                items={_items}
                farItems={_farItems}
                ariaLabel="Inbox actions"
                primaryGroupAriaLabel="Pool Icons"
                farItemsGroupAriaLabel="About"
            />
        </FocusTrapZone>
    );
};

const _items: ICommandBarItemProps[] = [
    {
        key: 'home',
        text: 'Home',
        iconProps: {iconName: 'Home'},
        href: "/"
    },
    {
        key: 'parameters',
        text: 'Parameters',
        iconProps: {iconName: 'Processing'},
        href: "/parameters"
    },
    {
        key: 'users',
        text: 'Users',
        iconProps: {iconName: 'FabricUserFolder'},
        href: "/users"
    },
    {
        key: 'pools',
        text: 'Pools',
        iconProps: {iconName: 'Sprint'},
        href: "/pools"
    },
    {
        key: 'designations',
        text: 'Designations',
        iconProps: {iconName: 'Teamwork'},
        href: "/designations"
    }
];

const _farItems: ICommandBarItemProps[] = [
    {
        key: 'info',
        text: 'Info',
        ariaLabel: 'Info',
        iconOnly: true,
        iconProps: {iconName: 'Info'},
        onClick: () => console.log('App Info'),
    }
];