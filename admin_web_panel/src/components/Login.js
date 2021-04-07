import React, { useState, useContext } from "react";
import { Redirect, Link } from "react-router-dom";
import { AdminContext, nullAdmin } from "../contexts/AdminContext";
import { fetchAdmin } from "../services/fetchAdmin";

const Login = () => {
    const [admin, setAdmin] = useContext(AdminContext);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isLoggingIn, setIsLoggingIn] = useState(false);
    const [errStatus, setErrStatus] = useState("");

    const loginAdmin = (e) => {
        e.preventDefault();
        if (!isLoggingIn) {
            setIsLoggingIn(true);
            setErrStatus("");

            fetchAdmin(
                username,
                password,
                (data) => {
                    setErrStatus("");
                    setIsLoggingIn(false);
                    setAdmin({ ...data, password: password });
                },
                (error) => {
                    setErrStatus(error.toString());
                    setIsLoggingIn(false);
                    setAdmin(nullAdmin);
                }
            );
        }
    };

    return admin.username !== null && admin.password !== null && admin.name !== null ? (
        <Redirect to="/" />
    ) : (
        <div className="Login">
            <div className="container">
                <div className="row">
                    <div className="one-third column">&emsp;</div>
                    <div className="one-third column">
                        <br />
                        <h6 className="text-center">
                            CS619 ~ IoT Based Smart Weather Reporting System ~ Admin Web Panel
                        </h6>
                        <hr />
                        <h3 className="text-center">Admin Login</h3>
                        <br />
                        <form onSubmit={loginAdmin}>
                            <label>Username</label>
                            <input
                                className="u-full-width"
                                type="text"
                                required
                                value={username}
                                placeholder="e.g. myusername"
                                autoComplete="off"
                                onChange={(e) => {
                                    setUsername(e.currentTarget.value);
                                }}
                                readOnly={isLoggingIn ? true : false}
                            />
                            <label>Password</label>
                            <input
                                className="u-full-width"
                                type="password"
                                required
                                value={password}
                                placeholder="e.g. p@55word"
                                autoComplete="off"
                                onChange={(e) => {
                                    setPassword(e.currentTarget.value);
                                }}
                                readOnly={isLoggingIn ? true : false}
                            />
                            <h6> </h6>
                            <input
                                className={isLoggingIn ? "u-full-width button" : "u-full-width button-primary"}
                                type="submit"
                                value={isLoggingIn ? "Logging in ..." : "Login"}
                            />
                            <p className="color-danger">{errStatus}</p>
                            <hr />
                            <p className="text-center">
                                <Link to="/set_server_ip/logged_out">Edit Server IP</Link>
                            </p>
                        </form>
                    </div>
                    <div className="one-third column">&emsp;</div>
                </div>
            </div>
        </div>
    );
};

export default Login;
