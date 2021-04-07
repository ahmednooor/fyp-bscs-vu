import React, { useState } from "react";
import { API_SERVER_IP } from "../services/apiConfigs";
import Header from "./Header";

const SetApiServerIP = (props) => {
    const [ip, setIp] = useState(API_SERVER_IP);

    const reloadWindowToSaveIP = (e) => {
        e.preventDefault();
        window.location.reload();
    };

    return (
        <>
            {props.match.params.loggedOut ? (
                <Header pageTitle="Server IP" loggedOut />
            ) : (
                <Header pageTitle="Server IP" />
            )}
            <div className="SetApiServerIP">
                <div className="container">
                    <div className="row">
                        <div className="four columns">&emsp;</div>
                        <div className="four columns text-center">
                            <h5>API Server IP</h5>
                            <pre>
                                <code>{ip}</code>
                            </pre>
                            <hr />
                            <form onSubmit={reloadWindowToSaveIP}>
                                <label className="text-left">Edit Server IP</label>
                                <input
                                    className="u-full-width"
                                    type="text"
                                    value={ip}
                                    placeholder="e.g. http://192.168.0.1:80"
                                    onChange={(e) => {
                                        setIp(e.target.value);
                                        localStorage.setItem("__api_server_ip", e.target.value);
                                    }}
                                />
                                <input className="u-full-width button" type="submit" value="Save" />
                            </form>
                        </div>
                        <div className="four columns">&emsp;</div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default SetApiServerIP;
