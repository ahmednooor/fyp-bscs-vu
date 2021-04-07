import React from "react";
import { Link } from "react-router-dom";

const DevicesTable = ({ devices }) => {
    return (
        <div className="DevicesTable">
            <div className="container">
                <div className="row">
                    <div className="twelve columns">
                        <h5>
                            Devices ( <strong>{devices !== null ? devices.length : 0}</strong> )
                        </h5>
                    </div>
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
                                {devices !== null
                                    ? devices.map((device) => {
                                          if (device === null) return null;
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
                                              <tr key={"/device/" + device_id.toString()}>
                                                  <td>
                                                      <Link to={"/device/" + device_id.toString()}>
                                                          <strong>{device_id}</strong>
                                                      </Link>
                                                  </td>
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
                                      })
                                    : null}
                            </tbody>
                        </table>
                        <br />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DevicesTable;
