/*
* COPYRIGHT 2012 MUSALA SOFT
*
* A file defining the schema of the articles table.
*/

CREATE TABLE articles (
  _id INTEGER PRIMARY KEY AUTOINCREMENT,
  article_id INTEGER UNIQUE NOT NULL,
  in_issue_number INTEGER,
  category_id INTEGER,
  template_id INTEGER,
  FOREIGN KEY(category_id) REFERENCES categories(category_id),
  FOREIGN KEY(template_id) REFERENCES templates(template_id));