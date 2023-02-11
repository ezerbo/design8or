import React from 'react';
import { render, screen } from '@testing-library/react';
import { Design8or } from './Design8or';

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Design8or />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});