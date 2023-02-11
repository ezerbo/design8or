import React from 'react';
import { render, screen } from '@testing-library/react';
import { Home } from './Home';

it('renders "Welcome to Your Fluent UI App"', () => {
  render(<Home />);
  const linkElement = screen.getByText(/Welcome to Your Fluent UI App/i);
  expect(linkElement).toBeInTheDocument();
});