import React from "react";
import { Link } from "react-router-dom";

const Header = ({ pageTitle, loggedOut }) => {
    return (
        <div className="Header">
            <div className="container">
                <br />
                <div className="row">
                    <div className="eight columns">
                        <p className="text-left">
                            CS619 ~ IoT Based Smart Weather Reporting System ~ Admin Web Panel
                            <br />
                            <strong>
                                / <em>{pageTitle}</em>
                            </strong>
                        </p>
                    </div>
                    <div className="four columns">
                        <nav className="text-right">
                            {loggedOut ? (
                                <Link to="/login">
                                    <strong>Login</strong>
                                </Link>
                            ) : (
                                <>
                                    <Link to="/">
                                        <strong>Dashboard</strong>
                                    </Link>{" "}
                                    &emsp;{" "}
                                    <Link to="/set_server_ip">
                                        <strong>Server IP</strong>
                                    </Link>
                                    &emsp;{" "}
                                    <Link to="/logout">
                                        <strong>Logout</strong>
                                    </Link>
                                </>
                            )}
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Header;
