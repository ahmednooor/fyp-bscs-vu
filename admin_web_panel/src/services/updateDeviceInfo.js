import { API_ROUTES } from "./apiConfigs";

export const updateDeviceInfo = (
    adminUsername,
    adminPassword,
    deviceId,
    devicePassword,
    userPassword,
    deviceName,
    isDeviceActive,
    onSuccessCallback,
    onErrorCallback
) => {
    fetch(API_ROUTES.updateDeviceInfoRoute, {
        method: "post",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
        body: JSON.stringify({
            admin_username: adminUsername,
            admin_password: adminPassword,
            device_id: deviceId,
            device_password: devicePassword,
            user_password: userPassword,
            device_name: deviceName,
            is_device_active: isDeviceActive,
        }),
    })
        .then((response) => {
            response.json().then((data) => {
                if (response.status === 200) {
                    onSuccessCallback(data);
                } else {
                    let errorMsg = response.status.toString() + " ";
                    if (data.message) {
                        errorMsg += data.message;
                    } else {
                        errorMsg += response.statusText;
                    }
                    onErrorCallback(errorMsg);
                }
            });
        })
        .catch((error) => {
            onErrorCallback(error);
        });
};
