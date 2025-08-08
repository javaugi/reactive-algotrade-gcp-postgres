import { render, screen } from '@testing-library/react';
import Button from './EditUserForm';

test('renders button with correct text', () => {
  render(<Button>Click me</Button>);
  const buttonElement = screen.getByRole('button', { name: /click me/i });
  expect(buttonElement).toBeInTheDocument();
});
