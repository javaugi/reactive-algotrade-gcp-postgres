// src/AppNavBar.js
import React, { useState } from 'react';
import { Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink } from 'reactstrap';
import { Link } from 'react-router-dom';

const AppNavbar = () => {
  const [isOpen, setIsOpen] = useState(false);

  const toggle = () => {
    setIsOpen(!isOpen);
  };

  return (
    <Navbar color="dark" dark expand="md">
      <NavbarBrand tag={Link} to="/">Home</NavbarBrand>
      <NavbarToggler onClick={toggle}/>
      <Collapse isOpen={isOpen} navbar>
        <Nav className="ml-auto" navbar>
          <NavItem>
            <NavLink href="https://www.linkedin.com/in/david-lee-838b86b/">David</NavLink>
          </NavItem>
          <NavItem>
            <NavLink href="https://github.com/javaugi">GitHub</NavLink>
          </NavItem>
        </Nav>
      </Collapse>
    </Navbar>
  );
};

export default AppNavbar;
