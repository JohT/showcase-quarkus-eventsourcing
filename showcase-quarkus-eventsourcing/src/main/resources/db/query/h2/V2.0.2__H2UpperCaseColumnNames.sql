-- Use upper case column names
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ALTER COLUMN "processorName" RENAME TO "PROCESSORNAME";
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ALTER COLUMN "segment" RENAME TO "SEGMENT";
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ALTER COLUMN "tokenType" RENAME TO "TOKENTYPE";
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ALTER COLUMN "eventtimestamp" RENAME TO "EVENTTIMESTAMP";
ALTER TABLE "axon_on_microprofile_query_tryout"."tokenentry" ALTER COLUMN "owner" RENAME TO "OWNER";