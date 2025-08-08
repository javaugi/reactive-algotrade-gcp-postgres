// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';

// Polyfills for Jest environment
global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

/*
Best Practices
    Query Priority: Prefer queries in this order:
        getByRole
        getByLabelText
        getByPlaceholderText
        getByText
        getByDisplayValue
        getByAltText
        getByTitle
        getByTestId
    Async Utilities: Use findBy* queries for elements that appear asynchronously
    User-Centric Tests: Test components the way users interact with them (not implementation details)
    Mocking: Use jest.mock() for complex dependencies
    Cleanup: No need to manually cleanup with React Testing Library v9+ (it does this automatically)

These examples cover the most common testing scenarios in React applications using React Testing Library. The library encourages testing 
    components in a way that resembles how users interact with your application, leading to more maintainable and reliable tests.
 
Key Points
    Test Location: Keep test files next to the components they test
    Naming Convention: Use .test.js or .test.jsx suffix

Automatic Execution: Tests will run:
    During npm run build (through the test:ci script)
    In your CI/CD pipeline
    When you run npm test locally
    Coverage Reports: The --coverage flag generates reports in /coverage
    This setup ensures your tests run automatically before builds and in your CI pipeline, helping catch issues early while maintaining a clean project structure.
 */