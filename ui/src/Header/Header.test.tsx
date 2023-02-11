import React from 'react';
import { render, screen } from '@testing-library/react';
import {Header} from "./Header";

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Header />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});