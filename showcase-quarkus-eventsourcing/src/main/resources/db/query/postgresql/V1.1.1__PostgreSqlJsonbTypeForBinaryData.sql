-- This script uses PostgreSql database specific column type "JSONB" for extended JSON support of binary data
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ADD COLUMN TOKEN jsonb;
