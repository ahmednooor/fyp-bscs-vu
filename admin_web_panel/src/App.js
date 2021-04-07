import React from "react";
import "./App.css";
import { HashRouter as Router, Switch, Route } from "react-router-dom";

import { AdminProvider } from "./contexts/AdminContext";
import { DevicesProvider } from "./contexts/DevicesContext";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import Logout from "./components/Logout";
import Device from "./components/Device";
import SetApiServerIP from "./components/SetApiServerIP";

function App() {
    return (
        <AdminProvider>
            <DevicesProvider>
                <div className="App">
                    <Router>
                        <Switch>
                            <Route path="/" exact component={Dashboard} />
                            <Route path="/login" exact component={Login} />
                            <Route path="/logout" exact component={Logout} />
                            <Route path="/set_server_ip" exact component={SetApiServerIP} />
                            <Route path="/set_server_ip/:loggedOut" exact component={SetApiServerIP} />
                            <Route path="/device/:deviceId" exact component={Device} />
                        </Switch>
                    </Router>
                </div>
            </DevicesProvider>
        </AdminProvider>
    );
}

export default App;
