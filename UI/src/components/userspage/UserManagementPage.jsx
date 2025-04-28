// Author: Ankit Kumar Sharma
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import UserService from "../service/UserService";
import { MdDelete } from "react-icons/md";
import { RxUpdate } from "react-icons/rx";
import { FiEdit } from "react-icons/fi";
import { AiOutlineUserAdd } from "react-icons/ai";

function UserManagementPage() {
  const [users, setUsers] = useState([]);
  useEffect(() => {
    fetchUsers();
  }, []);
  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await UserService.getAllUsers(token);
      setUsers(response.ourUsersList);
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  const deleteUser = async (userId) => {
    try {
      const confirmDelete = window.confirm(
        "Are you sure you want to delete this user?"
      );
      const token = localStorage.getItem("token");
      if (confirmDelete) {
        await UserService.deleteUser(userId, token);
        fetchUsers();
      }
    } catch (error) {
      console.error("Error deleting user:", error);
    }
  };

  return (
    <div className="user-management-container">
      <button className="reg-button">
        {" "}
        <Link to="/register">
          <AiOutlineUserAdd /> Add
        </Link>
      </button>
      <table>
        <thead>
          <tr>
            {/* <th>Client ID</th> */}
            <th>Name</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              {/* <td>{user.id}</td> */}
              <td style={{ textTransform: "uppercase" }}>{user.name}</td>
              <td>{user.email}</td>
              <td>
                <button
                  className="delete-button"
                  onClick={() => deleteUser(user.id)}
                >
                  <MdDelete />
                </button>
                <button className="update-btn-user">
                  <Link to={`/update-user/${user.id}`}>
                    <FiEdit />
                  </Link>
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
export default UserManagementPage;
