#!/bin/bash

set -e
set -u

echo "$POSTGRES_USER"
function create_user_and_database() {
	local database=$1
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
	    CREATE USER $database;
	    CREATE DATABASE $database;
	    GRANT ALL PRIVILEGES ON DATABASE $database TO $database;
      CREATE EXTENSION IF NOT EXISTS postgis;
EOSQL
}

echo "$POSTGRES_USER"

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
	for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
		create_user_and_database $db
		echo "Reindexing and refreshing collation for database: $db"
            psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$db" <<-EOSQL
                REINDEX DATABASE $db;
                ALTER DATABASE $db REFRESH COLLATION VERSION;
EOSQL
  done
fi
