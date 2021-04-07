from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS
from . import config

app = Flask(__name__)
# app.config.from_pyfile('config.py')

app.config["DEBUG"] = config.DEBUG
app.config["SECRET_KEY"] = config.SECRET_KEY
app.config["CORS_HEADERS"] = config.CORS_HEADERS
app.config["SQLALCHEMY_DATABASE_URI"] = config.SQLALCHEMY_DATABASE_URI
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = config.SQLALCHEMY_TRACK_MODIFICATIONS

cors = CORS(app)
db = SQLAlchemy(app)

from .routes import *
from .utils import *
from .models import *
