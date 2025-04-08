import React from 'react';
import {createRoot} from 'react-dom/client';
import {Design8or} from './Design8or';
import {FluentProvider, makeStyles, webLightTheme} from '@fluentui/react-components';
import reportWebVitals from './reportWebVitals';

// Inject some global styles
makeStyles({
  ':global(body,html,#root)': {
    margin: 0,
    padding: 0,
    height: '100vh',
  },
});

// @ts-ignore
const root = createRoot(document.getElementById('root'));

root.render(
    <FluentProvider theme={webLightTheme}>
      <Design8or />
    </FluentProvider>,
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
