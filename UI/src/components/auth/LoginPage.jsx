// Author: Ankit Kumar Sharma
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import UserService from "../service/UserService";
import { MdOutlineMailLock } from "react-icons/md";
import { MdLockPerson } from "react-icons/md";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const userData = await UserService.login(email, password);
      if (userData.token) {
        localStorage.setItem("token", userData.token);
        localStorage.setItem("role", userData.role);
        navigate("/profile");
        window.location.reload();
      } else {
        setError(userData.message);
      }
    } catch (error) {
      setError(error.message);
      setTimeout(() => {
        setError("");
      }, 5000);
    }
  };

  return (
    <div className="auth-container auth-container-login">
      <h2>
        <img
          src="./hitachi_logo_icon_168125.png"
          alt="Hitachi Logo"
          style={{ width: "50px" }}
        />
      </h2>
      {error && <p className="error-message">{error}</p>}
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>
            <MdOutlineMailLock />
            Email:{" "}
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter User Email"
          />
        </div>
        <div className="form-group">
          <label>
            <MdLockPerson />
            Password:{" "}
          </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter Password"
          />
        </div>
        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default LoginPage;
