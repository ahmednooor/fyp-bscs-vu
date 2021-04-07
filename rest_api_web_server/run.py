from api import app, db
from api.utils import populate_db_with_initial_data

if __name__ == "__main__":
    populate_db_with_initial_data()
    app.run('0.0.0.0', port=80)
