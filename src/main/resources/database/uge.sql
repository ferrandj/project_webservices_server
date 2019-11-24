CREATE TABLE product_type (
  id_product_type integer PRIMARY KEY AUTOINCREMENT,
  name text NOT NULL
);

CREATE TABLE product (
  id_product integer PRIMARY KEY AUTOINCREMENT,
  id_product_type integer NOT NULL,
  name text NOT NULL,
  image_url text NOT NULL,
  FOREIGN KEY (id_product_type) REFERENCES product_type (id_product_type)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE comment (
  id_comment integer PRIMARY KEY AUTOINCREMENT,
  id_product integer NOT NULL,
  mark integer NOT NULL,
  description text,
  CONSTRAINT mark_check CHECK (mark IN (0, 1, 2, 3, 4, 5)),
  FOREIGN KEY (id_product) REFERENCES product (id_product)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE user (
  id_user integer PRIMARY KEY AUTOINCREMENT,
  username text NOT NULL,
  password text NOT NULL,
  type integer NOT NULL,
  borrow_number integer NOT NULL DEFAULT 0,
  CONSTRAINT type_check CHECK (type IN (0, 1))/*0 = teacher, 1 = student*/
);

CREATE TABLE borrow (
  id_borrow integer PRIMARY KEY AUTOINCREMENT,
  id_user integer NOT NULL,
  id_product integer NOT NULL,
  state integer NOT NULL,
  asking_date Date NOT NULL DEFAULT (datetime('now','localtime')),
  borrowing_date Date,
  returning_date Date,
  CONSTRAINT state_check CHECK (state IN (0, 1, 2)), /* 0 = waiting in the borroewed list, 1 = is borrowed, 2 = returned */
  FOREIGN KEY (id_user) REFERENCES user (id_user)
	ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_product) REFERENCES product (id_product)
	ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO user (username, password, type) VALUES ("jojo", "jojo", 1);
INSERT INTO user (username, password, type) VALUES ("jerem", "jerem", 1);
INSERT INTO user (username, password, type) VALUES ("michel", "michel", 0);
INSERT INTO user (username, password, type) VALUES ("forax", "forax", 0);

INSERT INTO product_type (name) VALUES ("Vêtement");
INSERT INTO product_type (name) VALUES ("Livre");
INSERT INTO product_type (name) VALUES ("Jeu vidéo");
INSERT INTO product_type (name) VALUES ("Outil");
INSERT INTO product_type (name) VALUES ("Autre");

INSERT INTO product (id_product_type, name, image_url) VALUES (1, "T-shirt Star Wars", "https://images-na.ssl-images-amazon.com/images/I/61IpbkO3V8L._UX385_.jpg");
INSERT INTO product (id_product_type, name, image_url) VALUES (2, "The Witcher tome 1", "https://images-na.ssl-images-amazon.com/images/I/51gbaCE0GPL._SX307_BO1,204,203,200_.jpg");
INSERT INTO product (id_product_type, name, image_url) VALUES (4, "Bétonnière", "https://www.pointp.fr/asset/30/58/AST2243058-XL.jpg");

INSERT INTO comment (id_product, mark, description) VALUES (1, 1, "Trop grand, on dirait que Michmich l'a porté !");
INSERT INTO comment (id_product, mark) VALUES (3, 4);

INSERT INTO borrow (id_user, id_product, state, asking_date, borrowing_date, returning_date) VALUES (1, 1, 2, datetime("2019-11-02 13:37"), datetime("2019-11-02 13:37"), datetime("2019-11-06 14:37"));
INSERT INTO borrow (id_user, id_product, state, asking_date, borrowing_date) VALUES (3, 3, 1, datetime("2019-11-05 16:37"), datetime("2019-11-05 16:37"));
INSERT INTO borrow (id_user, id_product, state, asking_date) VALUES (2, 3, 0, datetime("2019-11-06 18:17"));
