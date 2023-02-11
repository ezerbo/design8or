import React from 'react';
import { render, screen } from '@testing-library/react';
import {Designations} from "./Designations";

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Designations />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});