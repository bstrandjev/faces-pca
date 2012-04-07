/*
* COPYRIGHT 2012 MUSALA SOFT
*
* A file defining the schema of the faces table.
*/

CREATE TABLE faces (
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  key TEXT UNIQUE NOT NULL,
  idx INTEGER UNIQUE,
  beautiful INTEGER DEFAULT -1);