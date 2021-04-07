import { API_ROUTES } from "./apiConfigs";

export const updateDeviceData = (
    adminUsername,
    adminPassword,
    deviceId,
    temperature,
    humidity,
    isRaining,
    rain,
    gpsLat,
    gpsLng,
    onSuccessCallback,
    onErrorCallback
) => {
    fetch(API_ROUTES.updateDeviceDataRoute, {
        method: "post",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
        body: JSON.stringify({
            admin_username: adminUsername,
            admin_password: adminPassword,
            device_id: deviceId,
            temperature_c: temperature,
            humidity: humidity,
            is_raining: isRaining,
            rain: rain,
            gps_lat: gpsLat,
            gps_lng: gpsLng,
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
