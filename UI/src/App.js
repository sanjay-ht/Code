// Author: Ankit Kumar Sharma
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./components/common/Navbar";
import LoginPage from "./components/auth/LoginPage";
import RegistrationPage from "./components/auth/RegistrationPage";
import FooterComponent from "./components/common/Footer";
import UserService from "./components/service/UserService";
import UpdateUser from "./components/userspage/UpdateUser";
import UserManagementPage from "./components/userspage/UserManagementPage";
import ProfilePage from "./components/userspage/ProfilePage";
import DashboardPage from "./components/userspage/DashboardPage";
import Loader from "./components/common/Loader";
import { useState, useEffect } from "react";

function App() {
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    const loaderShown = sessionStorage.getItem("loaderShown");

    if (!loaderShown) {
      const timer = setTimeout(() => {
        setIsLoading(false);
        sessionStorage.setItem("loaderShown", "true");
      }, 2000);

      return () => clearTimeout(timer);
    } else {
      setIsLoading(false);
    }
  }, []);

  return (
    <>
      {isLoading ? (
        <Loader></Loader>
      ) : (
        <BrowserRouter>
          <div className="App">
            <Navbar />
            <div className="content">
              <Routes>
                {UserService.userOnly() && (
                  <>
                    <Route path="/profile" element={<ProfilePage />} />
                    <Route path="/dashboard" element={<DashboardPage />} />
                  </>
                )}
                <Route exact path="/" element={<LoginPage />} />
                <Route exact path="/login" element={<LoginPage />} />
                {UserService.adminOnly() && (
                  <>
                    <Route path="/register" element={<RegistrationPage />} />
                    <Route
                      path="/admin/user-management"
                      element={<UserManagementPage />}
                    />
                    <Route
                      path="/update-user/:userId"
                      element={<UpdateUser />}
                    />
                  </>
                )}
                <Route path="*" element={<Navigate to="/login" />} />‰
              </Routes>
            </div>
            <FooterComponent />
          </div>
        </BrowserRouter>
      )}
    </>
  );
}
export default App;
