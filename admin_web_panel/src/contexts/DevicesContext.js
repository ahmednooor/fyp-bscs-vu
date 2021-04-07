import React, { useState } from "react";

export const DevicesContext = React.createContext(null);

export const DevicesProvider = ({ children }) => {
    const [devices, setDevices] = useState(null);

    return <DevicesContext.Provider value={[devices, setDevices]}>{children}</DevicesContext.Provider>;
};
