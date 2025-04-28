// Author: Ankit Kumar Sharma
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import UserService from "../service/UserService";
import { ImProfile } from "react-icons/im";
import { MdDashboardCustomize } from "react-icons/md";
import { MdManageAccounts } from "react-icons/md";
import { TbLogout } from "react-icons/tb";

function Navbar() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    UserService.isAuthenticated()
  );
  const [isAdmin, setIsAdmin] = useState(UserService.isAdmin());
  const handleLogout = () => {
    UserService.logout();
    setIsAuthenticated(false);
    setIsAdmin(false);
  };
  return (
    <nav>
      <ul>
        <img src="./hitachi-official-logo.png" alt="" className="companyLogo" />
        {!isAuthenticated && (
          <li>
            <Link to="/">Synthetic Monitoring</Link>
          </li>
        )}
        {!isAdmin && isAuthenticated && (
          <li>
            <Link to="/dashboard">
              <MdDashboardCustomize />
              Dashboard
            </Link>
          </li>
        )}
        {isAuthenticated && (
          <li>
            <Link to="/profile">
              <ImProfile />
              Profile
            </Link>
          </li>
        )}
        {isAdmin && (
          <li>
            <Link to="/admin/user-management">
              <MdManageAccounts />
              User Management
            </Link>
          </li>
        )}
        {isAuthenticated && (
          <li>
            <Link to="/" onClick={handleLogout} className="logOutBtn">
              <TbLogout />
            </Link>
          </li>
        )}
      </ul>
    </nav>
  );
}
export default Navbar;
