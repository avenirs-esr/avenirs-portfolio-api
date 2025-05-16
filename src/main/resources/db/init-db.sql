SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'template1'
  AND pid <> pg_backend_pid();

CREATE ROLE avenirs_api_admin_role SUPERUSER;
CREATE ROLE avenirs_api_admin PASSWORD 'ENC(nrhrW8giUqCjQzWRBDVj/XYVStp8Tgxs)' NOSUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
GRANT avenirs_api_admin_role to avenirs_api_admin;

CREATE DATABASE avenirs_api OWNER avenirs_api_admin;
GRANT ALL PRIVILEGES ON DATABASE avenirs_api TO avenirs_api_admin_role;
\c avenirs_api
CREATE SCHEMA IF NOT EXISTS dev AUTHORIZATION avenirs_api_admin;
ALTER USER avenirs_api_admin SET search_path TO dev, public;
CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
