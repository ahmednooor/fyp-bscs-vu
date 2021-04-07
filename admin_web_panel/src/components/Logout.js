import React, { useContext, useEffect } from "react";
import { Redirect } from "react-router-dom";
import { AdminContext, nullAdmin } from "../contexts/AdminContext";

const Logout = () => {
    const [, setAdmin] = useContext(AdminContext);

    useEffect(() => {
        setAdmin(nullAdmin);
    }, [setAdmin]);

    return <Redirect to="/" />;
};

export default Logout;
