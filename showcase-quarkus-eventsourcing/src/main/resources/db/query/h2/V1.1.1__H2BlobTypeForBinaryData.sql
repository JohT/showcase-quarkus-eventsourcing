-- This script uses H2 database specific column type "BLOB" for the TOKEN column
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ADD COLUMN TOKEN blob;
