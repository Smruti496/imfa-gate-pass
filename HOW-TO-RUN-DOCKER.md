# How to Run IMFA Gate Pass on Docker

## Prerequisites
- Docker Desktop installed and running
- Access to the existing PostgreSQL database

## Step 1: Configure environment

```bash
cp .env.example .env
```

Edit `.env` with your real DB credentials:
```
DB_URL=jdbc:postgresql://your-host:5432/your-database-name
DB_USER=your_db_username
DB_PASS=your_db_password
```

## Step 2: Run ALTER TABLE (one-time only)

Connect to your PostgreSQL database and run:
```sql
ALTER TABLE public.gate_pass
    ADD COLUMN IF NOT EXISTS pass_no        text,
    ADD COLUMN IF NOT EXISTS location       text,
    ADD COLUMN IF NOT EXISTS gate           text,
    ADD COLUMN IF NOT EXISTS check_in_time  text,
    ADD COLUMN IF NOT EXISTS check_out_time text;

-- For atomic pass number generation:
CREATE SEQUENCE IF NOT EXISTS gate_pass_seq START 1;
ALTER TABLE public.gate_pass ADD CONSTRAINT gate_pass_pass_no_uq UNIQUE (pass_no);
```

## Step 3: Build and start all services

```bash
docker compose up --build -d
```

First build takes ~5–10 minutes (downloads Maven/Node dependencies).

## Step 4: Access the application

Open: **http://localhost**

- Dashboard: http://localhost
- API: http://localhost/api/dashboard/stats

## Common commands

```bash
# View logs
docker compose logs -f

# View specific service logs
docker compose logs -f api
docker compose logs -f ui
docker compose logs -f nginx

# Stop all services
docker compose down

# Rebuild after code changes
docker compose up --build -d

# Check service health
docker compose ps
```

## Troubleshooting

**API fails to start:**
Check `docker compose logs api` — likely a DB connection issue. Verify DB credentials in `.env`.

**If your PostgreSQL is running locally (not in Docker):**
Use `host.docker.internal` instead of `localhost` in DB_URL:
```
DB_URL=jdbc:postgresql://host.docker.internal:5432/your-database-name
```

**Port 80 already in use:**
Change nginx port in `docker-compose.yml`: `"8090:80"` and access at http://localhost:8090
