// Author: Ankit Kumar Sharma
import React, { useState } from "react";
import UserService from "../service/UserService";
import { useNavigate } from "react-router-dom";
import { FaClipboard } from "react-icons/fa";

function RegistrationPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    role: "",
    city: "",
  });
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem("token");
      await UserService.register(formData, token);
      setFormData({
        name: "",
        email: "",
        password: "",
        role: "",
        city: "",
      });
      alert("User registered successfully");
      navigate("/admin/user-management");
    } catch (error) {
      alert("An error occurred while registering user");
    }
  };

  return (
    <div className="auth-container">
      <h2>
        <FaClipboard /> Fill Details
      </h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Name:</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Password:</label>
          <input
            type="password"
            name="password"
            value={formData.password}
            onChange={handleInputChange}
            required
          />
        </div>
        <div className="form-group">
          <label>Role:</label>
          <select name="role" id="role" onChange={handleInputChange} required>
            <option value="" disabled selected>
              --Select
            </option>
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
          </select>
        </div>
        <div className="form-group">
          <label>City:</label>
          <input
            type="text"
            name="city"
            value={formData.city}
            onChange={handleInputChange}
            placeholder="Enter your city"
            required
          />
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
  );
}
export default RegistrationPage;
