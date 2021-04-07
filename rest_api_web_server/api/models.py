from . import db
import json, datetime

class Admin(db.Model):
    row_id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(50), unique=True)
    password = db.Column(db.String(96))
    name = db.Column(db.String(50))

    def get_dict(self):
        return {
            "row_id": self.row_id,
            "username": self.username,
            "password": self.password,
            "name": self.name
        }

    def __repr__(self):
        return json.dumps(self.get_dict(), indent=4)


class Device(db.Model):
    row_id = db.Column(db.Integer, primary_key=True)
    device_id = db.Column(db.String(50), unique=True)
    password = db.Column(db.String(96), nullable=False)
    user_password = db.Column(db.String(96), nullable=False)
    is_active = db.Column(db.Boolean, nullable=False)
    name = db.Column(db.String(50))
    timestamp = db.Column(db.DateTime)
    temperature_c = db.Column(db.Float)
    humidity = db.Column(db.Float)
    is_raining = db.Column(db.Boolean)
    rain = db.Column(db.Float)
    gps_lat = db.Column(db.Float)
    gps_lng = db.Column(db.Float)

    def get_dict(self):
        return {
            "row_id": self.row_id,
            "device_id": self.device_id,
            "password": self.password,
            "user_password": self.user_password,
            "name": self.name,
            "is_active": self.is_active,
            "timestamp": self.timestamp,
            "temperature_c": self.temperature_c,
            "humidity": self.humidity,
            "is_raining": self.is_raining,
            "rain": self.rain,
            "gps_lat": self.gps_lat,
            "gps_lng": self.gps_lng
        }

    def __repr__(self):
        return json.dumps(self.get_dict(), indent=4)
