-- This is a SQL-script for createing the database needed for the project
CREATE SCHEMA awstracker;

CREATE TABLE IF NOT EXISTS Work (
	idwork VARCHAR(45) PRIMARY KEY, -- A value that represents the PK.
	date DATE, 						-- Specifies the date the item was created.
	description VARCHAR(400), 		-- Describes the item.
	guide VARCHAR(45), 				-- Represents the deliverable being worked on.
	status VARCHAR(400), 			-- Describes the status.
	username VARCHAR(45), 			-- The user who entered the item.
	archive TINYINT(4) 				-- Represents whether this is an active or archive item.
);
