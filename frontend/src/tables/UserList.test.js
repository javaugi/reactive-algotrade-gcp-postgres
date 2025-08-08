import { render, screen, waitFor } from '@testing-library/react';
import UserList from './UserList';

test('displays user data after loading', async () => {
  // Mock API response
  global.fetch = jest.fn(() =>
    Promise.resolve({
      json: () => Promise.resolve([{ id: 1, name: 'John Doe' }]),
    })
  );

  render(<UserList />);
  
  // Verify loading state
  expect(screen.getByText(/loading/i)).toBeInTheDocument();
  
  // Wait for data to load
  await waitFor(() => {
    expect(screen.getByText('John Doe')).toBeInTheDocument();
  });
  
  // Verify loading is gone
  expect(screen.queryByText(/loading/i)).not.toBeInTheDocument();
}); 
