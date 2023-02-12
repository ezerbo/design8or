import React from 'react';
import ReactDOM from 'react-dom';
import { Design8or } from './Design8or';
import { mergeStyles } from '@fluentui/react';
import reportWebVitals from './reportWebVitals';

// Inject some global styles
mergeStyles({
  ':global(body,html,#root)': {
    margin: 0,
    padding: 0,
    height: '100vh',
  },
});

ReactDOM.render(<Design8or />, document.getElementById('root'));

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();