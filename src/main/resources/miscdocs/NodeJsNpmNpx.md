https://dev.to/nermin_karapandzic/ive-created-an-open-source-spring-boot-nextjs-starter-kit-6fk
https://reactnative.dev/docs/getting-started  
https://www.tutorialspoint.com/react_native/index.htm
https://stacktobasics.com/deploy-nextjs-with-spring-boot
https://github.com/javaugi/c-shopping
https://github.com/javaugi/spring-boot-nextjs-starter-kit

check versions

node -v
npm -v
ng version

npm view react version
npm list react
npx next -v
npm list next

npm install or yarn install         - Install dependencies with 
npm run dev or yarn run dev         - start server
npm run build or yarn run build     - build prod bundle
npx kill-port 3000

rm -rf node_modules package-lock.json
npm cache clean --force
npm audit fix --force
npm install

npm list (for both react and angular)
npm fund
ng v (version)

Error with the latest version?
    nvm install 20
    nvm use 20
    Then delete node_modules and reinstall:
        rm -rf node_modules package-lock.json
        npm install

If You Must Use Node 22
    Try upgrading all dependencies (many packages don’t yet support Node 22):
        npm install -g npm@latest
        rm -rf node_modules package-lock.json
        npm install


1. download and install node.js or use nvm
    (1) download and install node.js
    (2)  Install nvm: a: curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
                      b:    export NVM_DIR="$HOME/.nvm"
                            source "$NVM_DIR/nvm.sh"
                      c: nvm --version
         if node.js not installed Once nvm is working:
                        nvm install 20
                        nvm use 20
                        nvm alias default 20


2. install npm: npm install -g npm
node -v
npm -v
3.  install angular: npm install -g @angular/cli
    ng new myangularapp
    npm start 
    or
    ng serve open
    http://localhost:4200/
4. install react native 
    npx react-native init myreactnative
    or 
    npx react-native@0.79.2 init myreactnative --version 0.79.2

  npx create-expo-app myreactnativeexpo  (or npx create-expo --template) - The fastest way to create universal React apps
    To run your project, navigate to the directory and run one of the following npm commands.
    - cd myreactnative
    - npm run android
    - npm run ios # you need to use macOS to build the iOS project - use the Expo app if you need to do iOS development without a Mac
    - npm run web

    https://reactnative.dev/docs/getting-started  
    https://www.tutorialspoint.com/react_native/index.htm
    https://stacktobasics.com/deploy-nextjs-with-spring-boot

5. install react js using vite framewor
    npm create vite@latest my-react-app -- --template react
    - to run
    npm run dev:  http://localhost:5173/
6. npx create-next-app@latest myreactnext - to create react js project using Next.js framwork
Ok to proceed? (y) y

Would you like to use TypeScript? ... No / Yes
Would you like to use ESLint? ... No / Yes
Would you like to use Tailwind CSS? ... No / Yes
Would you like your code inside a `src/` directory? ... No / Yes
Would you like to use App Router? (recommended) ... No / Yes
Would you like to use Turbopack for `next dev`? ... No / Yes
Would you like to customize the import alias (`@/*` by default)? ... No / Yes
Creating a new Next.js app in E:\springtutorial\cimathformulas\src\main\webapp\myreactnext.
Using npm.
Initializing project with template: app-tw
Installing dependencies:
- react
- react-dom
- next
Installing devDependencies:
- typescript
- @types/node
- @types/react
- @types/react-dom
- @tailwindcss/postcss
- tailwindcss
added 48 packages, and audited 49 packages in 33s

References
https://github.com/javaugi/c-shopping
https://github.com/javaugi/spring-boot-nextjs-starter-kit


6 packages are looking for funding
  run `npm fund` for details
    found 0 vulnerabilities
    Success! Created myreactnext at E:\springtutorial\cimathformulas\src\main\webapp\myreactnext

7. cd to any app created and find all elated packages and their versions
npm fund



Next.js is primarily used for web application development, offering features like server-side rendering and static site generation. 
React Native, on the other hand, is a framework for building native mobile applications. While they serve different purposes, they 
    can be used together in the same project to share code and create a unified development experience for web and mobile platforms.
Next.js can be used as a backend for a React Native app, providing API endpoints for data fetching and other server-side logic. 
    This allows developers to use the same language (JavaScript/TypeScript) for both the frontend (React Native) and the backend (Next.js).
    Additionally, with libraries like react-native-web, it's possible to share UI components and logic between a Next.js web app and a
    React Native mobile app. This approach enables cross-platform development, reducing development time and increasing maintainability.

While you can technically build a React app from scratch, using a framework like Create React App or Vite is highly recommended, especially 
    for new projects. These frameworks provide pre-configured environments, streamline the development process, and offer various benefits 
    like faster development servers and optimized builds. 
Here's a more detailed explanation:
Why use a framework?
    Simplified Setup: Frameworks handle the complexities of setting up a React project, including bundling, transpiling, and more. 
    Performance: Frameworks often include tools like webpack or Vite, which can optimize your app for faster loading and improved performance. 
    Community Support: Popular frameworks have large communities and extensive documentation, making it easier to find solutions to problems and learn new techniques. 
    Standard Practices: Frameworks often enforce good coding practices and architectural patterns, helping to create maintainable and scalable applications. 
When to consider building from scratch:
    Learning the Basics: Building from scratch can be a valuable learning experience, allowing you to understand how React works under the hood. 
    Highly Specialized Needs: If your application has very specific requirements that are not well-served by existing frameworks, you might consider building your own framework. 
    Testing and Learning: Some developers might choose to start from scratch to experiment with different tools and approaches. 
Popular React Frameworks:
    Create React App: A widely used framework for creating single-page applications, but it's currently deprecated and no longer recommended for new projects. 
    Vite:   A modern and fast build tool that can be used with React, offering a faster development experience. 
    Next.js: A framework for building full-fledged React applications with server-side rendering, static generation, and other advanced features. 
    Remix: A framework that focuses on the web's native capabilities, offering a different approach to React development. 
In summary: While you can build a React app without a framework, starting with one is generally recommended, especially for new projects. 
    Frameworks streamline development, offer performance advantages, and provide a robust foundation for building scalable applications. If you're 
    learning or have specific needs, you might consider building from scratch, but for most cases, a framework is the best approach. 

Choosing between Vite and Next.js for a React project depends on the project's specific requirements. Here's a comparison to help in the decision-making process:
Vite - Vite is a build tool that aims to provide a faster and leaner development experience for modern web projects. It excels in speed and simplicity, 
        making it suitable for:
    Small to medium-sized projects:
    . Vite is ideal for projects that don't require server-side rendering or complex routing.
    Single-page applications (SPAs):
    . Its fast refresh and build times make it efficient for developing SPAs.
    Component libraries:
    . Vite's simplicity and speed are beneficial for creating and testing UI components.
    Rapid prototyping:
    . The quick setup and minimal configuration allow for fast iteration and experimentation.
Next.js - Next.js is a full-fledged React framework that offers a range of features for building robust web applications. It's well-suited for:
    Large-scale applications:
    . Next.js provides the structure and tools needed for complex projects with many pages and components.
    SEO-heavy websites:
    . Server-side rendering (SSR) and static site generation (SSG) capabilities improve SEO performance.
    Dynamic content websites:
    . Features like incremental static regeneration (ISR) and API routes make it suitable for handling frequently updated content.
    Full-stack applications:
    . Next.js allows for building both the front-end and back-end within the same project.
    Applications requiring authentication and data fetching:
    . Next.js offers built-in solutions for these common requirements.
Key Differences    
            Feature                 Vite                                Next.js
Build Process           Fast, uses native ES modules                    More complex, server-side rendering
Rendering               Client-side                                     Server-side, static site generation, client-side
Routing                 Basic, relies on libraries like React Router    Built-in, advanced routing features
Data Fetching           Requires manual setup                           Built-in methods for data fetching
SEO                     Less optimized for SEO                          Highly optimized for SEO
Complexity              Simpler, easier to learn                        More complex, steeper learning curve
Project Size            Best for small to medium projects               Best for medium to large projects

Considerations
Project Size and Complexity:
    If the project is small or doesn't require server-side rendering, Vite is a great choice for its speed and simplicity. For larger, 
        more complex applications, Next.js offers the necessary structure and features.
SEO Requirements:
    If SEO is a priority, Next.js is the better option due to its server-side rendering and static site generation capabilities.
Development Speed:
    Vite's fast build times and hot module replacement can significantly speed up development, especially for front-end focused projects.
Learning Curve:
    Vite is easier to learn and set up, while Next.js has a steeper learning curve due to its more extensive feature set.
Conclusion
    The choice between Vite and Next.js depends on the specific needs of the project. Vite is ideal for smaller, client-side applications where 
        speed and simplicity are key, while Next.js is better suited for larger, more complex applications that require server-side rendering, 
        SEO optimization, and a full-featured framework.

Next.js is a React framework that enables developers to build full-stack web applications. It extends React's capabilities by providing
    features like server-side rendering (SSR), static site generation (SSG), and built-in routing. Next.js simplifies the development process 
    and offers performance optimizations, making it suitable for building scalable and SEO-friendly applications. 
Key Features of Next.js
    File-system routing:    Next.js uses a file-system-based router, where the structure of directories and files in the app directory defines the 
        application's routes.
    Server-side rendering (SSR) and Static Site Generation (SSG):   Next.js supports both SSR and SSG, allowing developers to choose the best 
        rendering strategy for each page.
    API routes:  Next.js allows developers to create API endpoints directly within the application using serverless functions.
    Built-in optimizations: Next.js includes built-in optimizations such as automatic code splitting, image optimization, and font optimization.
    Full-stack capabilities: Next.js can be used to build both the front-end and back-end of an application, making it a full-stack framework.
    React Server Components and Actions: Next.js leverages the latest React features, such as Server Components and Actions. 
Setting Up a Next.js Project
To create a new Next.js project, you can use the create-next-app CLI tool:
    npx create-next-app@latest my-nextjs-app
    cd my-nextjs-app
    This command sets up a basic Next.js project with the necessary files and directories.
Creating Pages and Routes
    In Next.js, pages are created as React components inside the app directory. For example, to create a page at /about, you would create a file
         named app/about/page.js (or page.jsx or page.tsx):

        // app/about/page.js
        export default function AboutPage() {
          return (
            <div>
              <h1>About Us</h1>
              <p>This is the about page.</p>
            </div>
          );
        }
    Next.js automatically handles the routing based on the file system, so you don't need to configure routes manually.
        Data Fetching: Next.js offers several ways to fetch data, including:
        Server Components: Data can be fetched directly within Server Components using async/await.
        Client Components: Data can be fetched in Client Components using hooks like useEffect or libraries like swr or react-query.
        API Routes: Data can be fetched from API routes defined within the app/api directory.
Deployment
Next.js applications can be deployed to various platforms, including:
    Vercel: Next.js is developed by Vercel and offers seamless integration with their platform.
    Netlify: Next.js applications can be deployed to Netlify using their build and deployment process.
    AWS, Google Cloud, Azure: Next.js applications can be deployed to cloud providers using Node.js or Docker containers.
Advantages of Using Next.js
    Improved performance: SSR and SSG can improve the performance and SEO of React applications.
    Simplified development: Next.js provides a structured and intuitive way to build applications, reducing the amount of configuration required.
    Full-stack capabilities: Next.js enables developers to build both the front-end and back-end of an application, simplifying the development process.
    Large and active community: Next.js has a large and active community, providing ample resources and support for developers


Create React App - We'll start by installing the project with create-react-app (CRA).
    
npx create-react-app react-hooks

Then run npm i.
Now you're all set with the React.
Inside that directory (react-hooks), you can run several commands:
  npm start:    Starts the development server.
  npm run build:     Bundles the app into static files for production.
  npm test:     Starts the test runner.
  npm run eject: Removes this tool and copies build dependencies, configuration files  and scripts into the app directory. If you do this, you can’t go back!

We suggest that you begin by typing:
  cd react-hooks
  npm start
