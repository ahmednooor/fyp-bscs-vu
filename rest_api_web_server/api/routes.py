from flask import Flask, request, jsonify, make_response
import uuid
from werkzeug.security import generate_password_hash, check_password_hash
import datetime
import json

from . import app, db
from .models import *
from .utils import *


@app.route("/api/v1/status", methods=["GET"])
def check_api_status():
    return jsonify({"message": "[OK] Api is running."}), 200


@app.route("/api/v1/admin/info/get", methods=["POST"])
def admin_gets_self_info():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    admin = admin.get_dict()
    del admin["row_id"]
    del admin["password"]

    return jsonify(admin), 200


@app.route("/api/v1/admin/info/update", methods=["POST"])
def admin_updates_self_info():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "admin_new_username",
         "admin_new_password", "admin_new_name"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("admin_new_username", json_payload["admin_new_username"], [str]),
        ("admin_new_password", json_payload["admin_new_password"], [str]),
        ("admin_new_name", json_payload["admin_new_name"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("admin_new_username", json_payload["admin_new_username"], [""]),
        ("admin_new_name", json_payload["admin_new_name"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    admin_username = json_payload["admin_username"]
    admin_password = json_payload["admin_password"]
    admin_new_username = json_payload["admin_new_username"]
    admin_new_password = json_payload["admin_new_password"]
    admin_new_name = json_payload["admin_new_name"]

    try:
        if admin_new_username != admin_username and \
                Admin.query.filter_by(
                    username=admin_new_username).first() is not None:
            return jsonify({"message": "Admin username is not available."}), 409
    except:
        return jsonify({"message": "Database Error."}), 500

    if admin_new_password == "":
        admin_new_password = admin_password

    admin.username = admin_new_username
    admin.password = generate_password_hash(admin_new_password, "sha256")
    admin.name = admin_new_name

    try:
        db.session.add(admin)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    admin = admin.get_dict()
    del admin["row_id"]
    del admin["password"]

    return jsonify(admin), 200


@app.route("/api/v1/admin/devices/get", methods=["POST"])
def admin_gets_all_devices():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    try:
        devices = Device.query.all()
    except:
        return jsonify({"message": "Database Error."}), 500

    response_json_body = {
        "devices": []
    }

    for device in devices:
        device_to_append = device.get_dict()
        del device_to_append["row_id"]
        del device_to_append["password"]
        del device_to_append["user_password"]
        response_json_body["devices"].append(device_to_append)

    return jsonify(response_json_body), 200


@app.route("/api/v1/admin/device/get", methods=["POST"])
def admin_gets_device():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "device_id"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("device_id", json_payload["device_id"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("device_id", json_payload["device_id"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    device_id = json_payload["device_id"]

    try:
        device = Device.query.filter_by(device_id=device_id).first()
        if device is None:
            return jsonify({"message": "Device not found."}), 404
    except:
        return jsonify({"message": "Database Error."}), 500

    device = device.get_dict()
    del device["row_id"]
    del device["password"]
    del device["user_password"]

    return jsonify(device), 200


@app.route("/api/v1/admin/device/new", methods=["POST"])
def admin_creates_new_device():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "device_id", "device_password",
         "user_password", "device_name", "is_device_active"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("device_id", json_payload["device_id"], [str]),
        ("device_password", json_payload["device_password"], [str]),
        ("user_password", json_payload["user_password"], [str]),
        ("device_name", json_payload["device_name"], [str]),
        ("is_device_active", json_payload["is_device_active"], [bool]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("device_id", json_payload["device_id"], [""]),
        ("device_password", json_payload["device_password"], [""]),
        ("user_password", json_payload["user_password"], [""]),
        ("device_name", json_payload["device_name"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid
    
    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    device_id = json_payload["device_id"]
    device_password = generate_password_hash(json_payload["device_password"],
                                             "sha256")
    user_password = generate_password_hash(json_payload["user_password"],
                                           "sha256")
    device_name = json_payload["device_name"]
    is_device_active = json_payload["is_device_active"]

    try:
        if Device.query.filter_by(device_id=device_id).first() is not None:
            return jsonify({"message": "Device already exists."}), 409

        device = Device(device_id=device_id, password=device_password,
                        user_password=user_password, name=device_name, 
                        is_active=is_device_active)
        db.session.add(device)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    device = device.get_dict()
    del device["row_id"]
    del device["password"]
    del device["user_password"]

    return jsonify(device), 201


@app.route("/api/v1/admin/device/update", methods=["POST"])
def admin_updates_device():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "device_id", "device_password", 
         "user_password", "device_name", "is_device_active"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("device_id", json_payload["device_id"], [str]),
        ("device_password", json_payload["device_password"], [str]),
        ("user_password", json_payload["user_password"], [str]),
        ("device_name", json_payload["device_name"], [str]),
        ("is_device_active", json_payload["is_device_active"], [bool]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("device_id", json_payload["device_id"], [""]),
        ("device_name", json_payload["device_name"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    device_id = json_payload["device_id"]
    device_password = json_payload["device_password"]
    user_password = json_payload["user_password"]
    device_name = json_payload["device_name"]
    is_device_active = json_payload["is_device_active"]

    try:
        device = Device.query.filter_by(device_id=device_id).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if device is None:
        return jsonify({"message": "Device not found."}), 404

    if device_password == "":
        device_password = device.password
    else:
        device_password = generate_password_hash(device_password, "sha256")
    
    if user_password == "":
        user_password = device.user_password
    else:
        user_password = generate_password_hash(user_password, "sha256")

    device.password = device_password
    device.user_password = user_password
    device.name = device_name
    device.is_active = is_device_active

    try:
        db.session.add(device)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    updated_device = device.get_dict()
    del updated_device["row_id"]
    del updated_device["password"]
    del updated_device["user_password"]

    return jsonify(updated_device), 200


@app.route("/api/v1/admin/device/delete", methods=["POST"])
def admin_deletes_device():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "device_id"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("device_id", json_payload["device_id"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("device_id", json_payload["device_id"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    device_id = json_payload["device_id"]

    try:
        device = Device.query.filter_by(device_id=device_id).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if device is None:
        return jsonify({"message": "Device not found."}), 404

    try:
        db.session.delete(device)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    return jsonify({"message": "Device has been deleted."}), 200


@app.route("/api/v1/admin/device/data/update", methods=["POST"])
def admin_updates_device_data():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "device_id", "temperature_c",
         "humidity", "is_raining", "rain", "gps_lat", "gps_lng"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("device_id", json_payload["device_id"], [str]),
        ("temperature_c", json_payload["temperature_c"], [int, float, NoneType]),
        ("humidity", json_payload["humidity"], [int, float, NoneType]),
        ("is_raining", json_payload["is_raining"], [bool, NoneType]),
        ("rain", json_payload["rain"], [int, float, NoneType]),
        ("gps_lat", json_payload["gps_lat"], [int, float, NoneType]),
        ("gps_lng", json_payload["gps_lng"], [int, float, NoneType]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("device_id", json_payload["device_id"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    device_id = json_payload["device_id"]
    temperature_c = json_payload["temperature_c"]
    humidity = json_payload["humidity"]
    is_raining = json_payload["is_raining"]
    rain = json_payload["rain"]
    gps_lat = json_payload["gps_lat"]
    gps_lng = json_payload["gps_lng"]

    try:
        device = Device.query.filter_by(device_id=device_id).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if device is None:
        return jsonify({"message": "Device not found."}), 404

    device.timestamp = get_current_pkt_timestamp()
    device.temperature_c = temperature_c
    device.humidity = humidity
    device.is_raining = is_raining
    device.rain = rain
    device.gps_lat = gps_lat
    device.gps_lng = gps_lng

    try:
        db.session.add(device)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    updated_device = device.get_dict()
    del updated_device["row_id"]
    del updated_device["password"]
    del updated_device["user_password"]

    return jsonify(updated_device), 200


@app.route("/api/v1/admin/device/data/reset", methods=["POST"])
def admin_resets_device_data():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["admin_username", "admin_password", "device_id"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("admin_username", json_payload["admin_username"], [str]),
        ("admin_password", json_payload["admin_password"], [str]),
        ("device_id", json_payload["device_id"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("admin_username", json_payload["admin_username"], [""]),
        ("admin_password", json_payload["admin_password"], [""]),
        ("device_id", json_payload["device_id"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    admin = get_authenticated_admin(json_payload["admin_username"],
                                    json_payload["admin_password"])
    if not isinstance(admin, Admin):
        return admin

    device_id = json_payload["device_id"]

    try:
        device = Device.query.filter_by(device_id=device_id).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if device is None:
        return jsonify({"message": "Device not found."}), 404

    device.timestamp = None
    device.temperature_c = None
    device.humidity = None
    device.is_raining = None
    device.rain = None
    device.gps_lat = None
    device.gps_lng = None

    try:
        db.session.add(device)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    updated_device = device.get_dict()
    del updated_device["row_id"]
    del updated_device["password"]
    del updated_device["user_password"]

    return jsonify(updated_device), 200


@app.route("/api/v1/user/device/data/get", methods=["POST"])
def user_gets_device_data():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["device_id", "user_password"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("device_id", json_payload["device_id"], [str]),
        ("user_password", json_payload["user_password"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("device_id", json_payload["device_id"], [""]),
        ("user_password", json_payload["user_password"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    device = get_authenticated_device_for_user(json_payload["device_id"],
                                               json_payload["user_password"])
    if not isinstance(device, Device):
        return device

    device_to_send = device.get_dict()
    del device_to_send["row_id"]
    del device_to_send["password"]
    del device_to_send["user_password"]

    return jsonify(device_to_send), 200


@app.route("/api/v1/device/data/get", methods=["POST"])
def device_gets_device_data():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["device_id", "device_password"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("device_id", json_payload["device_id"], [str]),
        ("device_password", json_payload["device_password"], [str]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("device_id", json_payload["device_id"], [""]),
        ("device_password", json_payload["device_password"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    device = get_authenticated_device(json_payload["device_id"],
                                      json_payload["device_password"])
    if not isinstance(device, Device):
        return device

    device_to_send = device.get_dict()
    del device_to_send["row_id"]
    del device_to_send["password"]
    del device_to_send["user_password"]

    return jsonify(device_to_send), 200


@app.route("/api/v1/device/data/update", methods=["POST"])
def device_updates_device_data():
    json_payload = request.get_json()
    
    if json_payload is None:
        return jsonify({"message": "No json body found."}), 400

    missing_params = check_missing_json_params(
        ["device_id", "device_password", "temperature_c", "humidity", 
         "is_raining", "rain", "gps_lat", "gps_lng"],
        json_payload)
    if missing_params is not None:
        return missing_params

    are_param_types_valid = validate_param_types([
        ("device_id", json_payload["device_id"], [str]),
        ("device_password", json_payload["device_password"], [str]),
        ("temperature_c", json_payload["temperature_c"], [int, float, NoneType]),
        ("humidity", json_payload["humidity"], [int, float, NoneType]),
        ("is_raining", json_payload["is_raining"], [bool, NoneType]),
        ("rain", json_payload["rain"], [int, float, NoneType]),
        ("gps_lat", json_payload["gps_lat"], [int, float, NoneType]),
        ("gps_lng", json_payload["gps_lng"], [int, float, NoneType]),
    ])
    if are_param_types_valid is not True:
        return are_param_types_valid
    
    are_param_values_valid = validate_param_values([
        ("device_id", json_payload["device_id"], [""]),
        ("device_password", json_payload["device_password"], [""]),
    ])
    if are_param_values_valid is not True:
        return are_param_values_valid

    device = get_authenticated_device(json_payload["device_id"],
                                      json_payload["device_password"])
    if not isinstance(device, Device):
        return device

    temperature_c = json_payload["temperature_c"]
    humidity = json_payload["humidity"]
    is_raining = json_payload["is_raining"]
    rain = json_payload["rain"]
    gps_lat = json_payload["gps_lat"]
    gps_lng = json_payload["gps_lng"]

    if not device.is_active:
        return jsonify({"message": "Device is disabled by admin."}), 403

    device.timestamp = get_current_pkt_timestamp()
    device.temperature_c = temperature_c
    device.humidity = humidity
    device.is_raining = is_raining
    device.rain = rain
    device.gps_lat = gps_lat
    device.gps_lng = gps_lng

    try:
        db.session.add(device)
        db.session.commit()
    except:
        return jsonify({"message": "Database Error."}), 500

    updated_device = device.get_dict()
    del updated_device["row_id"]
    del updated_device["password"]
    del updated_device["user_password"]

    return jsonify(updated_device), 200

