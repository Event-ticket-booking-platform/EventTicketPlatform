DO $$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_database WHERE datname = 'paymentservice'
   ) THEN
      PERFORM dblink_exec('dbname=postgres', 'CREATE DATABASE paymentservice');
   END IF;
END$$;
