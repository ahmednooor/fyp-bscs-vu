import { API_ROUTES } from "./apiConfigs";

export const updateAdmin = (
    adminUsername,
    adminPassword,
    adminNewUsername,
    adminNewPassword,
    adminNewName,
    onSuccessCallback,
    onErrorCallback
) => {
    fetch(API_ROUTES.updateAdminRoute, {
        method: "post",
        headers: {
            "Content-type": "application/json; charset=UTF-8",
        },
        body: JSON.stringify({
            admin_username: adminUsername,
            admin_password: adminPassword,
            admin_new_username: adminNewUsername,
            admin_new_password: adminNewPassword,
            admin_new_name: adminNewName,
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
