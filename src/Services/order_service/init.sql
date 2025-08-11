DO $$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'orderservice'
   ) THEN
      PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE orderservice');
   END IF;
END$$;
