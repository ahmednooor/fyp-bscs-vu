import React, { useContext, useEffect } from "react";
import { Redirect } from "react-router-dom";
import { AdminContext, nullAdmin } from "../contexts/AdminContext";
import { fetchAdmin } from "../services/fetchAdmin";
import Admin from "./Admin";
import { DevicesContext } from "../contexts/DevicesContext";
import { fetchAllDevices } from "../services/fetchAllDevices";
import Header from "./Header";
import NewDevice from "./NewDevice";
import DevicesTable from "./DevicesTable";

const Dashboard = ({ history }) => {
    const [admin, setAdmin] = useContext(AdminContext);
    // const [isAdminFetched, setIsAdminFetched] = useState(false);
    const [devices, setDevices] = useContext(DevicesContext);
    // const [areDevicesFetched, setAreDevicesFetched] = useState(false);

    useEffect(() => {
        if (admin.username !== null && admin.password !== null) {
            fetchAdmin(
                admin.username,
                admin.password,
                (data) => {
                    // setIsAdminFetched(true);
                    setAdmin({ ...data, password: admin.password });
                    fetchAllDevices(
                        admin.username,
                        admin.password,
                        (data) => {
                            // setAreDevicesFetched(true);
                            setDevices(data.devices);
                        },
                        (error) => {
                            // setAreDevicesFetched(true);
                            setDevices(null);
                        }
                    );
                },
                (error) => {
                    // setIsAdminFetched(true);
                    setAdmin(nullAdmin);
                }
            );
        }
        // if (devices === null && !areDevicesFetched) {
        // }
    }, [admin.username, admin.password, setAdmin, setDevices]);

    return admin.username === null || admin.password === null || admin.name === null ? (
        <Redirect to="/login" />
    ) : (
        <div className="Dashboard">
            <Header pageTitle="Dashboard" />
            <Admin />
            <NewDevice />
            <DevicesTable devices={devices} />
        </div>
    );
};

export default Dashboard;
