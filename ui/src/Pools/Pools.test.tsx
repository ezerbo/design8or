import React from 'react';
import { render, screen } from '@testing-library/react';
import {Pools} from "./Pools";

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Pools />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});