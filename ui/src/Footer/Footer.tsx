import React from "react";
import './Footer.css';

export const Footer: React.FunctionComponent = () => {

    return (
      <div className="footer">
         © {new Date().getFullYear()} Design8or
      </div>
    );
  };