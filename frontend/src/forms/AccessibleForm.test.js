import { render, screen } from '@testing-library/react';
import { axe } from 'jest-axe';
import AccessibleForm from './AccessibleForm';

test('has no accessibility violations', async () => {
  const { container } = render(<AccessibleForm />);
  const results = await axe(container);
  expect(results).toHaveNoViolations();
});

test('has proper aria labels', () => {
  render(<AccessibleForm />);
  expect(screen.getByRole('textbox', { name: /username/i })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /submit/i })).toBeInTheDocument();
});
