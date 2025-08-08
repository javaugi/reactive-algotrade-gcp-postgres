import { render, screen } from '@testing-library/react';
import { ThemeProvider } from '../context/ThemeContext';
import ThemedButton from './ThemedButton';

test('uses theme from context', () => {
  render(
    <ThemeProvider value="dark">
      <ThemedButton />
    </ThemeProvider>
  );
  
  const button = screen.getByRole('button');
  expect(button).toHaveClass('dark-theme');
});
