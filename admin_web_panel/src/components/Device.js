import React, { useState, useContext, useEffect, useCallback } from "react";
import { Link, Redirect } from "react-router-dom";
import { AdminContext } from "../contexts/AdminContext";
import { updateDeviceInfo } from "../services/updateDeviceInfo";
import { fetchDevice } from "../services/fetchDevice";
import Header from "./Header";
import { resetDeviceData } from "../services/resetDeviceData";
import { deleteDevice } from "../services/deleteDevice";
import { updateDeviceData } from "../services/updateDeviceData";

const Device = (props) => {
    const [admin] = useContext(AdminContext);
    const [device, setDevice] = useState(null);
    const [isDeviceFetched, setIsDeviceFetched] = useState(false);

    const [deviceId, setDeviceId] = useState(props.match.params.deviceId);
    const [devicePassword, setDevicePassword] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const [deviceName, setDeviceName] = useState("");
    const [isDeviceActive, setIsDeviceActive] = useState(false);

    const [temperature, setTemperature] = useState("");
    const [humidity, setHumidity] = useState("");
    const [isRaining, setIsRaining] = useState(false);
    const [rain, setRain] = useState("");
    const [gpsLat, setGpsLat] = useState("");
    const [gpsLng, setGpsLng] = useState("");
    const [timestamp, setTimestamp] = useState("");

    const [isDeviceInfoSaving, setIsDeviceInfoSaving] = useState(false);
    const [isDeviceDataSaving, setIsDeviceDataSaving] = useState(false);
    const [isDeviceDataResetting, setIsDeviceDataResetting] = useState(false);
    const [isDeviceDeleting, setIsDeviceDeleting] = useState(false);
    const [isDeviceDeleted, setIsDeviceDeleted] = useState(false);
    const [errStatusDeviceInfo, setErrStatusDeviceInfo] = useState("");
    const [errStatusDeviceData, setErrStatusDeviceData] = useState("");
    const [errStatusDeviceResetDelete, setErrStatusDeviceResetDelete] = useState("");

    const callFetchDeviceService = useCallback(() => {
        fetchDevice(
            admin.username,
            admin.password,
            deviceId,
            (data) => {
                setErrStatusDeviceInfo("");
                setIsDeviceFetched(true);
                setDevice(data);
            },
            (error) => {
                setErrStatusDeviceInfo(error.toString());
                setIsDeviceFetched(true);
                setDevice(null);
            }
        );
    }, [admin.username, admin.password, deviceId]);

    useEffect(() => {
        callFetchDeviceService();
    }, [callFetchDeviceService]);

    useEffect(() => {
        if (device !== null) {
            setDeviceId(device.device_id);
            setDevicePassword("");
            setDeviceName(device.name);
            setIsDeviceActive(device.is_active);
            setTemperature(device.temperature_c);
            setHumidity(device.humidity);
            setIsRaining(device.is_raining);
            setRain(device.rain);
            setGpsLat(device.gps_lat);
            setGpsLng(device.gps_lng);
            setTimestamp(device.timestamp);
        }
    }, [device]);

    const resetDeviceDataHandler = (e) => {
        e.preventDefault();
        if (!window.confirm("!!! Are you sure you want to Reset this Device?")) return;
        if (!isDeviceDataResetting) {
            setIsDeviceDataResetting(true);
            setErrStatusDeviceResetDelete("");

            resetDeviceData(
                admin.username,
                admin.password,
                deviceId,
                (data) => {
                    setErrStatusDeviceResetDelete("");
                    setIsDeviceDataResetting(false);
                    callFetchDeviceService();
                },
                (error) => {
                    setErrStatusDeviceResetDelete(error.toString());
                    setIsDeviceDataResetting(false);
                    callFetchDeviceService();
                }
            );
        }
    };

    const updateDeviceDataHandler = (e) => {
        e.preventDefault();
        if (!isDeviceDataSaving) {
            setIsDeviceDataSaving(true);
            setErrStatusDeviceData("");

            updateDeviceData(
                admin.username,
                admin.password,
                deviceId,
                parseFloat(temperature),
                parseFloat(humidity),
                isRaining,
                parseFloat(rain),
                parseFloat(gpsLat),
                parseFloat(gpsLng),
                (data) => {
                    setErrStatusDeviceData("");
                    setIsDeviceDataSaving(false);
                    callFetchDeviceService();
                },
                (error) => {
                    setErrStatusDeviceData(error.toString());
                    setIsDeviceDataSaving(false);
                    callFetchDeviceService();
                }
            );
        }
    };

    const deleteDeviceHandler = (e) => {
        e.preventDefault();
        if (!window.confirm("!!! Are you sure you want to Delete this Device?")) return;
        if (!isDeviceDataResetting) {
            setIsDeviceDeleting(true);
            setErrStatusDeviceResetDelete("");

            deleteDevice(
                admin.username,
                admin.password,
                deviceId,
                (data) => {
                    setErrStatusDeviceResetDelete("");
                    setIsDeviceDeleting(false);
                    setIsDeviceDeleted(true);
                },
                (error) => {
                    setErrStatusDeviceResetDelete(error.toString());
                    setIsDeviceDeleting(false);
                    setIsDeviceDeleted(false);
                    callFetchDeviceService();
                }
            );
        }
    };

    const updateDeviceInfoHandler = (e) => {
        e.preventDefault();
        if (!isDeviceInfoSaving) {
            setIsDeviceInfoSaving(true);
            setErrStatusDeviceInfo("");

            updateDeviceInfo(
                admin.username,
                admin.password,
                deviceId,
                devicePassword,
                userPassword,
                deviceName,
                isDeviceActive,
                (data) => {
                    setErrStatusDeviceInfo("");
                    setIsDeviceInfoSaving(false);
                    setDevicePassword("");
                    setUserPassword("");
                    callFetchDeviceService();
                },
                (error) => {
                    setErrStatusDeviceInfo(error.toString());
                    setIsDeviceInfoSaving(false);
                    callFetchDeviceService();
                }
            );
        }
    };

    return admin.username === null || admin.password === null || admin.name === null ? (
        <Redirect to="/login" />
    ) : isDeviceDeleted ? (
        <Redirect to="/" />
    ) : (
        <>
            <Header pageTitle="Device Details" />
            <div className="Device">
                <div className="container">
                    <div className="row">
                        <div className="twelve columns">
                            {!isDeviceFetched ? <h6>Loading ...</h6> : null}
                            <h5>
                                Device ID:{" "}
                                <Link to={"/device/" + deviceId}>
                                    <strong>{deviceId}</strong>
                                </Link>
                            </h5>
                        </div>
                    </div>
                    <form onSubmit={updateDeviceInfoHandler}>
                        <div className="row">
                            <div className="twelve columns">
                                <label>Device Name</label>
                                <input
                                    className="u-full-width"
                                    type="text"
                                    value={deviceName}
                                    placeholder="e.g. My Device"
                                    onChange={(e) => {
                                        setDeviceName(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceInfoSaving ? true : false}
                                />
                            </div>
                        </div>
                        <div className="row">
                            <div className="four columns">
                                <label>Device Password</label>
                                <input
                                    className="u-full-width"
                                    type="text"
                                    value={devicePassword}
                                    placeholder="Empty means old password"
                                    onChange={(e) => {
                                        setDevicePassword(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceInfoSaving ? true : false}
                                />
                                <small>
                                    Leave Device Password Field empty if you don't want to change device password.
                                </small>
                            </div>
                            <div className="four columns">
                                <label>User Password</label>
                                <input
                                    className="u-full-width"
                                    type="text"
                                    value={userPassword}
                                    placeholder="Empty means old password"
                                    onChange={(e) => {
                                        setUserPassword(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceInfoSaving ? true : false}
                                />
                                <small>
                                    Leave User Password Field empty if you don't want to change user password.
                                </small>
                            </div>
                            <div className="two columns">
                                <label>Device Status</label>
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={isDeviceActive}
                                        onChange={(e) => {
                                            setIsDeviceActive(!isDeviceActive);
                                        }}
                                    />
                                    <span className="label-body">Active</span>
                                </label>
                            </div>
                            <div className="two columns">
                                <label>&emsp;</label>
                                <input
                                    className={
                                        isDeviceInfoSaving ? "u-full-width button" : "u-full-width button-primary"
                                    }
                                    type="submit"
                                    value={isDeviceInfoSaving ? "Saving ..." : "Save"}
                                />
                            </div>
                        </div>
                    </form>
                    <div className="twelve columns">
                        {errStatusDeviceInfo !== "" ? <p className="color-danger">{errStatusDeviceInfo}</p> : null}
                        <hr />
                    </div>
                </div>
                <div className="container">
                    <div className="row">
                        <div className="twelve columns">
                            <h5>Device Data</h5>
                        </div>
                    </div>
                    <form onSubmit={updateDeviceDataHandler}>
                        <div className="row">
                            <div className="two columns">
                                <label>Temperature (C)</label>
                                <input
                                    className="u-full-width"
                                    type="number"
                                    step="0.01"
                                    value={temperature === null ? "" : temperature}
                                    placeholder="e.g. 23.5"
                                    onChange={(e) => {
                                        setTemperature(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceDataSaving ? true : false}
                                />
                                {temperature === null ? <small>null</small> : null}
                            </div>

                            <div className="two columns">
                                <label>Humidity (%)</label>
                                <input
                                    className="u-full-width"
                                    type="number"
                                    step="0.01"
                                    value={humidity === null ? "" : humidity}
                                    placeholder="e.g. 23.5"
                                    onChange={(e) => {
                                        setHumidity(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceDataSaving ? true : false}
                                />
                                {humidity === null ? <small>null</small> : null}
                            </div>

                            <div className="two columns">
                                <label>Rain Status</label>
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={isRaining || false}
                                        onChange={(e) => {
                                            setIsRaining(!isRaining);
                                        }}
                                    />
                                    <span className="label-body">Raining</span>
                                </label>
                                {isRaining === null ? <small>null</small> : null}
                            </div>

                            <div className="two columns">
                                <label>Rain (mm)</label>
                                <input
                                    className="u-full-width"
                                    type="number"
                                    step="0.01"
                                    value={rain === null ? "" : rain}
                                    placeholder="e.g. 23.5"
                                    onChange={(e) => {
                                        setRain(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceDataSaving ? true : false}
                                />
                                {rain === null ? <small>null</small> : null}
                            </div>

                            <div className="two columns">
                                <label>GPS Lat</label>
                                <input
                                    className="u-full-width"
                                    type="number"
                                    step="0.01"
                                    value={gpsLat === null ? "" : gpsLat}
                                    placeholder="e.g. 23.5"
                                    onChange={(e) => {
                                        setGpsLat(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceDataSaving ? true : false}
                                />
                                {gpsLat === null ? <small>null</small> : null}
                            </div>

                            <div className="two columns">
                                <label>GPS Lng</label>
                                <input
                                    className="u-full-width"
                                    type="number"
                                    step="0.01"
                                    value={gpsLng === null ? "" : gpsLng}
                                    placeholder="e.g. 23.5"
                                    onChange={(e) => {
                                        setGpsLng(e.currentTarget.value);
                                    }}
                                    readOnly={isDeviceDataSaving ? true : false}
                                />
                                {gpsLng === null ? <small>null</small> : null}
                            </div>
                        </div>
                        <div className="row">
                            <div className="ten columns">
                                <label>&emsp;</label>
                                <p>
                                    Last Updated (Timestamp): <strong>{timestamp || "null"}</strong>
                                </p>
                            </div>
                            <div className="two columns">
                                <label>&emsp;</label>
                                <input
                                    className={
                                        isDeviceDataSaving ? "u-full-width button" : "u-full-width button-primary"
                                    }
                                    type="submit"
                                    value={isDeviceDataSaving ? "Saving ..." : "Save"}
                                />
                            </div>
                        </div>
                        <div className="row">
                            <div className="twelve columns">
                                {errStatusDeviceData !== "" ? (
                                    <p className="color-danger">{errStatusDeviceData}</p>
                                ) : null}
                                <hr />
                            </div>
                        </div>
                    </form>
                    <div className="row">
                        <div className="twelve columns">
                            <table className="u-full-width">
                                <thead>
                                    <tr>
                                        <th>Device ID</th>
                                        <th>Device Name</th>
                                        <th>Device Status</th>
                                        <th>Temperature (C)</th>
                                        <th>Humidity (%)</th>
                                        <th>Rain Status</th>
                                        <th>Rain (mm)</th>
                                        <th>GPS Lat</th>
                                        <th>GPS Lng</th>
                                        <th>Last Updated (Timestamp)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {device !== null
                                        ? (() => {
                                              const device_id = device.device_id === null ? "null" : device.device_id;
                                              const name = device.name === null ? "null" : device.name;
                                              const is_active =
                                                  device.is_active === null
                                                      ? "null"
                                                      : device.is_active === true
                                                      ? "Active"
                                                      : "Disabled";
                                              const temperature_c =
                                                  device.temperature_c === null ? "null" : device.temperature_c;
                                              const humidity = device.humidity === null ? "null" : device.humidity;
                                              const is_raining =
                                                  device.is_raining === null
                                                      ? "null"
                                                      : device.is_raining === true
                                                      ? "Raining"
                                                      : "No Rain";
                                              const rain = device.rain === null ? "null" : device.rain;
                                              const gps_lat = device.gps_lat === null ? "null" : device.gps_lat;
                                              const gps_lng = device.gps_lng === null ? "null" : device.gps_lng;
                                              const timestamp = device.timestamp === null ? "null" : device.timestamp;
                                              return (
                                                  <tr>
                                                      <td>{device_id}</td>
                                                      <td>{name}</td>
                                                      <td>{is_active}</td>
                                                      <td>{temperature_c}</td>
                                                      <td>{humidity}</td>
                                                      <td>{is_raining}</td>
                                                      <td>{rain}</td>
                                                      <td>{gps_lat}</td>
                                                      <td>{gps_lng}</td>
                                                      <td>{timestamp}</td>
                                                  </tr>
                                              );
                                          })()
                                        : null}
                                </tbody>
                            </table>
                            <p className="text-right">
                                {gpsLat && gpsLng ? (
                                    <a
                                        href={"https://maps.google.com/?q=" + gpsLat + "," + gpsLng + ""}
                                        target="_blank"
                                    >
                                        Show Location on Map &#8599;
                                    </a>
                                ) : null}
                            </p>
                            <br />
                        </div>
                    </div>
                </div>
                <div className="container">
                    <div className="row">
                        <div className="three columns">
                            <input
                                className={"u-full-width button"}
                                type="button"
                                value={isDeviceDataResetting ? "Resetting ..." : "Reset Device Data"}
                                onClick={resetDeviceDataHandler}
                            />
                        </div>
                        <div className="three columns">
                            <input
                                className={"u-full-width button button-danger"}
                                type="button"
                                value={isDeviceDeleting ? "Deleting ..." : "Delete Device"}
                                onClick={deleteDeviceHandler}
                            />
                        </div>
                    </div>
                    <div className="row">
                        <div className="twelve columns">
                            {errStatusDeviceResetDelete !== "" ? (
                                <p className="color-danger">{errStatusDeviceResetDelete}</p>
                            ) : null}
                            <hr />
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Device;
