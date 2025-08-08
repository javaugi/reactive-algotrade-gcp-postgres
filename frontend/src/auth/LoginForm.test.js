import { render, screen, fireEvent } from '@testing-library/react';
import LoginForm from './LoginForm';

test('submits form with email and password', async () => {
  const mockSubmit = jest.fn();
  render(<LoginForm onSubmit={mockSubmit} />);
  
  // Fill out the form
  fireEvent.change(screen.getByLabelText(/email/i), {
    target: { value: 'user@example.com' }
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: 'password123' }
  });
  
  // Submit the form
  fireEvent.click(screen.getByRole('button', { name: /sign in/i }));
  
  // Assertions
  expect(mockSubmit).toHaveBeenCalledWith({
    email: 'user@example.com',
    password: 'password123'
  });
});
