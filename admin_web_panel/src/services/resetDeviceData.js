import { API_ROUTES } from "./apiConfigs";

export const resetDeviceData = (adminUsername, adminPassword, deviceId, onSuccessCallback, onErrorCallback) => {
    fetch(API_ROUTES.resetDeviceDataRoute, {
        method: "post",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
        body: JSON.stringify({
            admin_username: adminUsername,
            admin_password: adminPassword,
            device_id: deviceId,
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
