import React from 'react';
import { render, screen } from '@testing-library/react';
import {Users} from "./Users";

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Users />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});