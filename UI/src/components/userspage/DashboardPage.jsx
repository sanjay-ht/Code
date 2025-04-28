// Author: Ankit Kumar Sharma
import React, { useState } from "react";
import UserService from "../service/UserService";
import { useEffect } from "react";
function DashboardPage() {
  const [dashboardUrl, setDashboardUrl] = useState("");
  useEffect(() => {
    fetchProfileInfo();
  }, []);
  const fetchProfileInfo = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await UserService.getYourProfile(token);
      setDashboardUrl(response.ourUsers.dashboardUrl);
    } catch (error) {
    }
  };
  return (
    <>
      <div className="dashboardPageContainer">
        <div
          className="frameContent"
          style={{ width: "100%", height: "100vh" }}
        >
          <iframe
            src={dashboardUrl}
            frameborder="0"
            width="100%"
            height="100%"
            style={{ marginBottom: "69px" }}
          ></iframe>
        </div>
        <div className="overlayDiv1"></div>
        <div className="overlayDiv2"></div>
      </div>
    </>
  );
}
export default DashboardPage;
