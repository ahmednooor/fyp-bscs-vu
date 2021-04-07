export const API_SERVER_IP = localStorage.getItem("__api_server_ip")
    ? localStorage.getItem("__api_server_ip")
    : "http://localhost:80";

localStorage.setItem("__api_server_ip", API_SERVER_IP);

export const API_ROUTES = {
    fetchAdminRoute: API_SERVER_IP + "/api/v1/admin/info/get",
    updateAdminRoute: API_SERVER_IP + "/api/v1/admin/info/update",

    fetchAllDevicesRoute: API_SERVER_IP + "/api/v1/admin/devices/get",
    addNewDeviceRoute: API_SERVER_IP + "/api/v1/admin/device/new",
    fetchDeviceRoute: API_SERVER_IP + "/api/v1/admin/device/get",
    updateDeviceInfoRoute: API_SERVER_IP + "/api/v1/admin/device/update",
    updateDeviceDataRoute: API_SERVER_IP + "/api/v1/admin/device/data/update",
    deleteDeviceRoute: API_SERVER_IP + "/api/v1/admin/device/delete",
    resetDeviceDataRoute: API_SERVER_IP + "/api/v1/admin/device/data/reset",
};
