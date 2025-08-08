import { render, screen, fireEvent } from '@testing-library/react';
import Login from './Login';
import { AuthProvider } from './AuthContext';

test('renders login form', () => {
  render(
    <AuthProvider>
      <Login />
    </AuthProvider>
  );
  
  expect(screen.getByLabelText(/username/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
});

test('submits login form', async () => {
  const mockLogin = jest.fn();
  
  render(
    <AuthProvider value={{ login: mockLogin }}>
      <Login />
    </AuthProvider>
  );
  
  fireEvent.change(screen.getByLabelText(/username/i), {
    target: { value: 'testuser' }
  });
  fireEvent.change(screen.getByLabelText(/password/i), {
    target: { value: 'password123' }
  });
  fireEvent.click(screen.getByRole('button', { name: /login/i }));
  
  expect(mockLogin).toHaveBeenCalledWith({
    username: 'testuser',
    password: 'password123'
  });
});
