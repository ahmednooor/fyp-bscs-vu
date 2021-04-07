from flask import jsonify
import datetime
from werkzeug.security import generate_password_hash, check_password_hash

from . import db
from .models import Admin, Device
from . import config

NoneType = type(None)

def populate_db_with_initial_data():
    db.create_all()
    admin = Admin.query.filter_by(username="admin").first()
    if admin is None:
        username = config.DEFAULT_ADMIN_USERNAME
        password = generate_password_hash(config.DEFAULT_ADMIN_PASSWORD,
                                          "sha256")
        name = config.DEFAULT_ADMIN_NAME
        admin = Admin(username=username, password=password, name=name)
        db.session.add(admin)
        db.session.commit()

def get_current_pkt_timestamp():
    return datetime.datetime.utcnow() + datetime.timedelta(hours=5)

def check_missing_json_params(params, json_payload):
    missing_params = []
    are_params_missing = False
    
    for param in params:
        if param not in json_payload:
            are_params_missing = True
            missing_params.append("'" + param + "'")
    
    if not are_params_missing:
        return None
    
    return jsonify({"message": "Missing " + ", ".join(missing_params) + 
                               " in json body."}), 400

def get_authenticated_admin(admin_username, admin_plain_password):
    try:
        admin = Admin.query.filter_by(username=admin_username).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if admin is None:
        return jsonify({"message": "Admin not found."}), 404

    if not check_password_hash(admin.password, admin_plain_password):
        return jsonify({"message": "Invalid admin username or password."}), 401

    return admin

def get_authenticated_device(device_id, device_plain_password):
    try:
        device = Device.query.filter_by(device_id=device_id).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if device is None:
        return jsonify({"message": "Device not found."}), 404

    if not check_password_hash(device.password, device_plain_password):
        return jsonify({"message": "Invalid device ID or password."}), 401

    return device

def get_authenticated_device_for_user(device_id, user_plain_password):
    try:
        device = Device.query.filter_by(device_id=device_id).first()
    except:
        return jsonify({"message": "Database Error."}), 500

    if device is None:
        return jsonify({"message": "Device not found."}), 404

    if not check_password_hash(device.user_password, user_plain_password):
        return jsonify({"message": "Invalid device ID or password."}), 401

    return device

def validate_param_types(param_types_tuples):
    """ param_types_tuples = [
            (param_name, param_value, acceptable_types_list),
            ...
        ] """
    for param_tuple in param_types_tuples:
        param_name = param_tuple[0]
        param_value = param_tuple[1]
        param_types = param_tuple[2]

        is_invalid = True

        if type(param_value) in param_types:
            is_invalid = False

        if is_invalid:
            return jsonify(
                {"message": "Invalid '" + param_name + "' type. Expected " + 
                            str(param_types) + ""}), 400

    return True

def validate_param_values(param_values_tuple):
    """ param_values_tuple = [
            (param_name, param_value, unacceptable_values_list),
            ...
        ] """
    for param_tuple in param_values_tuple:
        param_name = param_tuple[0]
        param_value = param_tuple[1]
        invalid_values = param_tuple[2]

        is_invalid = False

        if param_value in invalid_values:
            is_invalid = True

        if is_invalid:
            return jsonify(
                {"message": "Invalid '" + param_name + "' value. Can not be " + 
                            str(invalid_values) + ""}), 400

    return True

