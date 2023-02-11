import React from 'react';
import { render, screen } from '@testing-library/react';
import {Parameters} from "./Parameters";

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Parameters />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});