CREATE TABLE product_type (
  id_product_type long PRIMARY KEY,
  name text NOT NULL
);

CREATE TABLE product (
  id_product long PRIMARY KEY,
  id_product_type long NOT NULL,
  name text NOT NULL,
  image_url text NOT NULL,
  FOREIGN KEY (id_product_type) REFERENCES product_type (id_product_type)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE comment (
  id_comment long PRIMARY KEY,
  id_product long NOT NULL,
  mark int NOT NULL,
  description text,
  CONSTRAINT mark_check CHECK (mark IN (0, 1, 2, 3, 4, 5)),
  FOREIGN KEY (id_product) REFERENCES producte (id_product)
	ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE user (
  id_user long PRIMARY KEY,
  username text NOT NULL,
  password text NOT NULL,
  type int NOT NULL,
  borrow_number long NOT NULL DEFAULT 0,
  CONSTRAINT type_check CHECK (type IN (0, 1))/*0 = teacher, 1 = student*/
);

CREATE TABLE borrow (
  id_borrow long PRIMARY KEY,
  id_user long NOT NULL,
  id_product long NOT NULL,
  state int NOT NULL,
  asking_date Date NOT NULL DEFAULT (datetime('now','localtime')),
  borrowing_date Date,
  returning_date Date,
  CONSTRAINT state_check CHECK (state IN (0, 1, 2)), /* 0 = waiting in the borroewed list, 1 = is borrowed, 2 = returned */
  FOREIGN KEY (id_user) REFERENCES user (id_user)
	ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (id_product) REFERENCES product (id_product)
	ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO user VALUES (1, "jojo", "jojo", 1, 0);
INSERT INTO user VALUES (2, "jerem", "jerem", 1, 0);
INSERT INTO user VALUES (3, "michel", "michel", 0, 0);
INSERT INTO user VALUES (4, "forax", "forax", 0, 0);

INSERT INTO product_type VALUES (1, "Vêtement");
INSERT INTO product_type VALUES (2, "Livre");
INSERT INTO product_type VALUES (3, "Jeu vidéo");
INSERT INTO product_type VALUES (4, "Outil");
INSERT INTO product_type VALUES (5, "Autre");

INSERT INTO product VALUES (1, 1, "T-shirt Star Wars", "https://images-na.ssl-images-amazon.com/images/I/61IpbkO3V8L._UX385_.jpg");
INSERT INTO product VALUES (2, 2, "The Witcher tome 1", "https://images-na.ssl-images-amazon.com/images/I/51gbaCE0GPL._SX307_BO1,204,203,200_.jpg");
INSERT INTO product VALUES (3, 4, "Bétonnière", "https://www.pointp.fr/asset/30/58/AST2243058-XL.jpg");

INSERT INTO comment VALUES (1, 1, 1, "Trop grand, on dirait que Michmich l'a porté !");
INSERT INTO comment VALUES (2, 3, 4, NULL);

INSERT INTO borrow VALUES (1, 1, 1, 2, datetime("2019-11-02 13:37"), datetime("2019-11-02 13:37"), datetime("2019-11-06 14:37"));
INSERT INTO borrow VALUES (2, 3, 3, 1, datetime("2019-11-05 16:37"), datetime("2019-11-05 16:37"), null);
INSERT INTO borrow VALUES (3, 2, 3, 0, datetime("2019-11-06 18:17"), null, null);
