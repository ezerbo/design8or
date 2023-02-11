import React from 'react';
import { render, screen } from '@testing-library/react';
import {Footer} from "./Footer";

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Footer />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});