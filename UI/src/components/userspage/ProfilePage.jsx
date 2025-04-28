// Author: Ankit Kumar Sharma
import React, { useState, useEffect } from "react";
import UserService from "../service/UserService";
import { Link } from "react-router-dom";
import { RxUpdate } from "react-icons/rx";
import { MdOutlineMailOutline } from "react-icons/md";
import { MdOutlineLocationCity } from "react-icons/md";
import { MdOutlineFileUpload } from "react-icons/md";
import { IoClose } from "react-icons/io5";
import { MdAdminPanelSettings } from "react-icons/md";
import { LuCopy } from "react-icons/lu";
import { LuCopyCheck } from "react-icons/lu";
import { IoMdEye } from "react-icons/io";
import { RiCalendarScheduleLine } from "react-icons/ri";
import { RiCalendarScheduleFill } from "react-icons/ri";
import { LuFileJson2 } from "react-icons/lu";
import { FiDownload } from "react-icons/fi";
import { BsSave2 } from "react-icons/bs";
import { RxResume } from "react-icons/rx";
import { FaRegPauseCircle } from "react-icons/fa";
import "react-calendar/dist/Calendar.css";

function ProfilePage() {
  const [profileInfo, setProfileInfo] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);
  const [code, setCode] = useState("");
  const [desc, setDesc] = useState("");
  const [id, setId] = useState(0); //user id
  const [scenarios, setScenarios] = useState([]);
  const [jsonData, setJsonData] = useState(0);
  const [viewJson, setViewJSON] = useState(false);
  const [isCopied, setIsCopied] = useState(false);
  const [isExecutionStarted, setIsExecutionStarted] = useState(true);
  const [isScenarioVisible, setScenarioVisible] = useState(false);
  const [isSContainerVisible, setIsSContainer] = useState(false);
  const [frequencey, setFrequency] = useState(0);
  const [startDateTime, setStartDateTime] = useState("");
  const [endDateTime, setEndDateTime] = useState("");
  const [scheduleError, setScheduleError] = useState("");
  const [scenarioId, setScenarioId] = useState(0);

  useEffect(() => {
    fetchProfileInfo();
  }, []);

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!selectedFile) {
      alert("Please select a file first.");
      return;
    }
    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("id", id);
    formData.append("code", code);
    formData.append("desc", desc);

    try {
      const token = localStorage.getItem("token");
      const response = await UserService.fileUpload(formData, token);
      window.location.reload();
    } catch (error) {
      console.error("Error uploading the file", error);
    }
  };

  const fetchProfileInfo = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await UserService.getYourProfile(token);
      setId(response.ourUsers.id);
      setScenarios(response.ourUsers.scenarios);
      setProfileInfo(response.ourUsers);
    } catch (error) {
      console.error("Error fetching profile information:", error);
    }
  };
  const handleDownloadJson = (s_id) => {
    for (let i = 0; i < scenarios.length; i++) {
      if (scenarios[i].scenario_id === s_id) {
        setJsonData(scenarios[i].jsonFile);
      }
    }
    const blob = new Blob([JSON.stringify(jsonData)], {
      type: "application/json",
    });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `json_data_${s_id}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };
  const handleExecuteJson = async () => {
    if (isExecutionStarted === false) {
      setIsExecutionStarted(true);
      return;
    }
    setIsExecutionStarted(false);
    const token = localStorage.getItem("token");
    const res = await UserService.executeJson(id, token);
  };
  const handleViewJSON = (s_id) => {
    for (let i = 0; i < scenarios.length; i++) {
      if (scenarios[i].scenario_id === s_id) {
        setJsonData(scenarios[i].jsonFile);
      }
    }
    setViewJSON(true);
  };
  const closeJSONView = () => {
    setScenarioVisible(false);
  };
  const closeJSONPreview = () => {
    setViewJSON(false);
    setIsCopied(false);
  };
  const handleVisibleJsonContainer = () => {
    setScenarioVisible(true);
    setIsCopied(false);
  };
  const copyToClipBoard = () => {
    navigator.clipboard
      .writeText(JSON.stringify(jsonData))
      .then(() => {
        setIsCopied(true);
      })
      .catch((err) => {
        console.log("Unable to copy the Json");
        setIsCopied(false);
      });
  };
  const closeSchedulerView = () => {
    setIsSContainer(false);
  };
  const openSchedulerView = (s_id) => {
    console.log(s_id);
    setScenarioId(s_id);
    setIsSContainer(true);
  };
  const handleScheduleSumbit = async (e) => {
    e.preventDefault();
    console.log(
      "Frequency" +
        " " +
        frequencey +
        " " +
        startDateTime +
        " " +
        endDateTime +
        " scenario_id " +
        scenarioId +
        " " +
        id
    );
    const formData = new FormData();
    formData.append("scenarioId", scenarioId);
    formData.append("frequency", frequencey);
    formData.append("sdt", startDateTime);
    formData.append("edt", endDateTime);
    formData.append("userId", id);
    try {
      const token = localStorage.getItem("token");
      const response = await UserService.addSchedule(formData, token);
      if (response.statusCode == 200) {
        window.location.reload();
      }
    } catch (error) {
      console.error("Error uploading the file", error);
    }
  };
  const convertDateFormat = (millisecondsTime) => {
    const date = new Date(millisecondsTime);
    const utcString = date.toUTCString();
    const modifiedDateString = utcString.replace(" GMT", "");
    return modifiedDateString;
  };
  const handleStopAction = async (sId, e, status) => {
    e.preventDefault();
    let state = status;
    if (state === "Active") {
      state = "Inactive";
    } else if (state === "Inactive") {
      state = "Active";
    }
    const formData = new FormData();
    formData.append("scenarioId", sId);
    formData.append("userId", id);
    formData.append("state", state);
    try {
      const token = localStorage.getItem("token");
      const response = await UserService.stopResumeScheduleForScenario(
        formData,
        token
      );
      console.log(response);
      if (response.data.statusCode == 200) {
        window.location.reload();
      }
    } catch (error) {
      console.error("Error uploading the stopping/resuming execution", error);
    }
  };

  return (
    <div className="profile-page-container">
      {isSContainerVisible ? (
        <>
          <div className="scheduleContainer">
            <span
              className="closeSchedulerView"
              onClick={closeSchedulerView}
              style={{
                fontSize: "30px",
                cursor: "pointer",
              }}
            >
              <IoClose />
            </span>
            <div className="subSchedulerContainer">
              <h2 style={{ color: "white" }}>
                <RiCalendarScheduleLine />
                Schedule
              </h2>
              <form onSubmit={handleScheduleSumbit}>
                <input
                  type="number"
                  placeholder="Frequency In Minutes"
                  className="json-field frequencey"
                  name="frequency"
                  id="frequency"
                  onChange={(e) => {
                    setFrequency(e.target.value);
                  }}
                />
                <br />
                <label htmlFor="startDateInput">Start Date and Time: </label>
                <input
                  type="datetime-local"
                  className="startDateInput"
                  id="startDateInput"
                  onChange={(e) => {
                    setStartDateTime(e.target.value);
                  }}
                />
                <br />
                <label htmlFor="endDateTimeInput">End Date and Time:</label>
                <input
                  type="datetime-local"
                  name="endDateTimeInput"
                  id="endDateTimeInput"
                  onChange={(e) => {
                    setEndDateTime(e.target.value);
                  }}
                />
                <p style={{ color: "red" }}>{scheduleError} </p>
                <button className="scheduleSubmitBtn" type="submit">
                  <BsSave2 />
                  Save
                </button>
              </form>
            </div>
          </div>
        </>
      ) : null}

      <h2 style={{ textTransform: "uppercase", fontWeight: "700" }}>
        <MdAdminPanelSettings />
        <span className="profileUserName">{profileInfo.name}</span>
      </h2>
      <p>
        <MdOutlineMailOutline />
        Mail: {profileInfo.email}
      </p>
      <p>
        <MdOutlineLocationCity />
        City: {profileInfo.city}
      </p>
      {profileInfo.role === "ADMIN" && (
        <button>
          <Link to={`/update-user/${profileInfo.id}`}>
            <RxUpdate />
            Update
          </Link>
        </button>
      )}
      {profileInfo.role !== "ADMIN" && (
        <button
          onClick={handleVisibleJsonContainer}
          style={{
            padding: "5px 0px 5px 0px",
            fontSize: "20px",
          }}
        >
          <span>
            <MdOutlineFileUpload />
          </span>
          Scenario
        </button>
      )}
      {viewJson ? (
        <div className="viewJsonContainer">
          <span className="copyJSONView" onClick={copyToClipBoard}>
            {isCopied ? <LuCopyCheck /> : <LuCopy />}
          </span>
          <span
            className="closeJSONView"
            onClick={closeJSONPreview}
            style={{
              fontSize: "30px",
              cursor: "pointer",
            }}
          >
            <IoClose />
          </span>
          <div className="viewJSONChild"> {JSON.stringify(jsonData)}</div>
        </div>
      ) : null}
      <div className="calenderContainer"></div>
      <div className="profile-page-btn-container">
        {isScenarioVisible ? (
          <div className="upload-json-container">
            <span
              className="closeJSONView"
              onClick={closeJSONView}
              style={{
                fontSize: "30px",
                cursor: "pointer",
              }}
            >
              <IoClose />
            </span>
            <div className="upload-json-subcontainer">
              <form onSubmit={handleSubmit}>
                <h2 style={{ color: "white" }}>
                  <LuFileJson2 />
                  Scenario
                </h2>
                <input
                  type="text"
                  placeholder="Code"
                  name="code"
                  className="json-field json-code"
                  onChange={(e) => {
                    setCode(e.target.value);
                  }}
                />
                <br />
                <input
                  type="text"
                  placeholder="Description"
                  name="description"
                  className="json-field json-description"
                  onChange={(e) => {
                    setDesc(e.target.value);
                  }}
                />
                <br />

                <input
                  type="file"
                  name="file-upload"
                  id="file-upload"
                  accept=".json"
                  onChange={handleFileChange}
                />
                <button className="jsonuploadBtn" type="submit">
                  <BsSave2 />
                  Save
                </button>
              </form>
            </div>
          </div>
        ) : null}

        {profileInfo.role !== "ADMIN" && scenarios.length > 0 ? (
          <>
            <br />

            <table className="userScenarioTable">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Description</th>
                  <th>Schedule Information</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {scenarios.map((scenario) => (
                  <tr key={scenario.scenario_id}>
                    <td>{scenario.code}</td>
                    <td style={{ maxWidth: "100px" }}>
                      {scenario.description}
                    </td>
                    <td>
                      <div>
                        <p>
                          Frequency:{" "}
                          {scenario.schedule
                            ? scenario.schedule["frequency"] + " " + "minutes"
                            : "NA"}{" "}
                        </p>
                        <p>
                          Start Date and Time:{" "}
                          {scenario.schedule
                            ? convertDateFormat(
                                scenario.schedule["startTimeInMillis"]
                              )
                            : "NA"}{" "}
                        </p>
                        <p>
                          End Date and Time:{" "}
                          {scenario.schedule
                            ? convertDateFormat(
                                scenario.schedule["endTimeInMillis"]
                              )
                            : "NA"}{" "}
                        </p>
                      </div>
                    </td>
                    <td className="actionContainerSchedule">
                      <button
                        onClick={() => {
                          handleViewJSON(scenario.scenario_id);
                        }}
                      >
                        <IoMdEye />
                        Preview
                      </button>
                      <button
                        onClick={() => handleDownloadJson(scenario.scenario_id)}
                      >
                        <FiDownload />
                        Download
                      </button>

                      <button
                        onClick={() => openSchedulerView(scenario.scenario_id)}
                      >
                        <RiCalendarScheduleFill />
                        Schedule
                      </button>
                      {scenario.schedule ? (
                        <>
                          <button
                            onClick={(e) => {
                              handleStopAction(
                                scenario.scenario_id,
                                e,
                                scenario.status
                              );
                            }}
                          >
                            {scenario.status == "Active" ? (
                              <>
                                <span style={{ color: "white" }}>
                                  <FaRegPauseCircle />
                                  Stop
                                </span>
                              </>
                            ) : (
                              <>
                                <span style={{ color: "white" }}>
                                  {/* #1ff91f */}
                                  <RxResume />
                                  Resume
                                </span>
                              </>
                            )}
                          </button>
                        </>
                      ) : null}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </>
        ) : profileInfo.role !== "ADMIN" ? (
          <p>No Scenarios are avaialable to display.</p>
        ) : null}
      </div>
    </div>
  );
}
export default ProfilePage;
