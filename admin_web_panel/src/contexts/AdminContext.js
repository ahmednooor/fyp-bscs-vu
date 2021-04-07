import React, { useState, useEffect } from "react";

export const nullAdmin = {
    username: null,
    password: null,
    name: null,
};

const initialAdminState = localStorage.getItem("__admin")
    ? JSON.parse(localStorage.getItem("__admin"))
    : JSON.parse(JSON.stringify(nullAdmin));

export const AdminContext = React.createContext(initialAdminState);

export const AdminProvider = ({ children }) => {
    const [admin, setAdmin] = useState(initialAdminState);

    useEffect(() => {
        localStorage.setItem("__admin", JSON.stringify(admin));
    }, [admin]);

    return <AdminContext.Provider value={[admin, setAdmin]}>{children}</AdminContext.Provider>;
};
