import React, { useState, useContext, useEffect } from "react";
import { AdminContext } from "../contexts/AdminContext";
import { addNewDevice } from "../services/addNewDevice";
import { DevicesContext } from "../contexts/DevicesContext";
import { fetchAllDevices } from "../services/fetchAllDevices";

const NewDevice = (props) => {
    const [admin] = useContext(AdminContext);
    const [, setDevices] = useContext(DevicesContext);

    const [deviceId, setDeviceId] = useState("");
    const [devicePassword, setDevicePassword] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const [deviceName, setDeviceName] = useState("");
    const [isDeviceActive, setIsDeviceActive] = useState(false);

    const [isDeviceInfoSaving, setIsDeviceInfoSaving] = useState(false);
    const [errStatusDeviceInfo, setErrStatusDeviceInfo] = useState("");

    useEffect(() => {}, []);

    const addNewDeviceSubmit = (e) => {
        e.preventDefault();
        if (!isDeviceInfoSaving) {
            setIsDeviceInfoSaving(true);
            setErrStatusDeviceInfo("");

            addNewDevice(
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
                    setDeviceId("");
                    setDevicePassword("");
                    setUserPassword("");
                    setDeviceName("");
                    setIsDeviceActive(false);

                    fetchAllDevices(
                        admin.username,
                        admin.password,
                        (data) => {
                            setDevices(data.devices);
                        },
                        (error_) => {
                            setDevices(null);
                        }
                    );
                },
                (error) => {
                    setErrStatusDeviceInfo(error.toString());
                    setIsDeviceInfoSaving(false);
                }
            );
        }
    };

    return (
        <div className="NewDevice">
            <div className="container">
                <div className="row">
                    <div className="twelve columns">
                        <h5>Add New Device</h5>
                    </div>
                </div>
                <form onSubmit={addNewDeviceSubmit}>
                    <div className="row">
                        <div className="four columns">
                            <label>Device ID</label>
                            <input
                                className="u-full-width"
                                type="text"
                                required
                                value={deviceId}
                                placeholder="e.g. d001"
                                onChange={(e) => {
                                    setDeviceId(e.currentTarget.value);
                                }}
                                readOnly={isDeviceInfoSaving ? true : false}
                            />
                        </div>
                        <div className="eight columns">
                            <label>Device Name</label>
                            <input
                                className="u-full-width"
                                type="text"
                                required
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
                                required
                                value={devicePassword}
                                placeholder="e.g. p@55word"
                                onChange={(e) => {
                                    setDevicePassword(e.currentTarget.value);
                                }}
                                readOnly={isDeviceInfoSaving ? true : false}
                            />
                        </div>
                        <div className="four columns">
                            <label>User Password</label>
                            <input
                                className="u-full-width"
                                type="text"
                                required
                                value={userPassword}
                                placeholder="e.g. p@55word"
                                onChange={(e) => {
                                    setUserPassword(e.currentTarget.value);
                                }}
                                readOnly={isDeviceInfoSaving ? true : false}
                            />
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
                                className={isDeviceInfoSaving ? "u-full-width button" : "u-full-width button-primary"}
                                type="submit"
                                value={isDeviceInfoSaving ? "Adding ..." : "Add"}
                            />
                        </div>
                    </div>
                </form>
                <div className="row">
                    <div className="twelve columns">
                        {errStatusDeviceInfo !== "" ? <p className="color-danger">{errStatusDeviceInfo}</p> : null}
                        <hr />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default NewDevice;
