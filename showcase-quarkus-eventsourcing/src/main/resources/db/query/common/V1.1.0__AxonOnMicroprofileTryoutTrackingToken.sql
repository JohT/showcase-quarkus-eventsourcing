CREATE SCHEMA IF NOT EXISTS "axon_on_microprofile_query_tryout";

CREATE TABLE "axon_on_microprofile_query_tryout"."tokenentry" (
	processorName VARCHAR(255) NOT NULL,
	segment INTEGER NOT NULL,
	-- token BLOB NULL, -> needs to be added afterwards inside the database specific setups
	tokenType VARCHAR(255) NULL,
	timestamp VARCHAR(255) NULL,
	owner VARCHAR(255) NULL,
	PRIMARY KEY (processorName,segment)
);