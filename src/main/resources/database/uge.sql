CREATE TABLE product_type
(
    id_product_type integer PRIMARY KEY AUTOINCREMENT,
    name            text NOT NULL
);

CREATE TABLE product
(
    id_product      integer PRIMARY KEY AUTOINCREMENT,
    id_product_type integer NOT NULL,
    name            text    NOT NULL,
    image_url       text    NOT NULL,
    price           integer NOT NULL,
    adding_date     Date    NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (id_product_type) REFERENCES product_type (id_product_type)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE comment
(
    id_comment   integer PRIMARY KEY AUTOINCREMENT,
    id_product   integer NOT NULL,
    id_user      integer NOT NULL,
    mark         int     NOT NULL,
    description  text,
    comment_date date    NOT NULL DEFAULT (datetime('now', 'localtime')),
    CONSTRAINT mark_check CHECK (mark IN (0, 1, 2, 3, 4, 5)),
    FOREIGN KEY (id_product) REFERENCES product (id_product)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_user) REFERENCES user (id_user)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE user
(
    id_user       integer PRIMARY KEY AUTOINCREMENT,
    username      text    NOT NULL,
    password      text    NOT NULL,
    type          integer NOT NULL,
    borrow_number long    NOT NULL DEFAULT 0,
    CONSTRAINT type_check CHECK (type IN (0, 1))/*0 = teacher, 1 = student*/
);

CREATE TABLE borrow
(
    id_borrow      integer PRIMARY KEY AUTOINCREMENT,
    id_user        integer NOT NULL,
    id_product     integer NOT NULL,
    state          integer NOT NULL,
    asking_date    Date    NOT NULL DEFAULT (datetime('now', 'localtime')),
    borrowing_date Date,
    returning_date Date,
    CONSTRAINT state_check CHECK (state IN (0, 1, 2)), /* 0 = waiting in the borroewed list, 1 = is borrowed, 2 = returned */
    FOREIGN KEY (id_user) REFERENCES user (id_user)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_product) REFERENCES product (id_product)
        ON DELETE CASCADE ON UPDATE CASCADE
);

/* UGE SellService */
CREATE TABLE customer
(
    id_customer integer PRIMARY KEY AUTOINCREMENT,
    username    text NOT NULL,
    password    text NOT NULL
);

CREATE TABLE chosen_product
(
    id_chosen_product integer PRIMARY KEY AUTOINCREMENT,
    id_customer       integer NOT NULL,
    id_product        text    NOT NULL,
    FOREIGN KEY (id_customer) REFERENCES customer (id_customer)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_product) REFERENCES product (id_product)
        ON DELETE CASCADE ON UPDATE CASCADE
);

/* UGE Banque */

CREATE TABLE bank_account
(
    id_bank_account integer PRIMARY KEY AUTOINCREMENT,
    id_customer     integer NOT NULL UNIQUE,
    wallet          integer NOT NULL,
    currency        text    NOT NULL,
    FOREIGN KEY (id_customer) REFERENCES customer (id_customer)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE notification
(
    id_user   integer NOT NULL,
    id_borrow integer NOT NULL,
    FOREIGN KEY (id_user) REFERENCES user (id_user)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_borrow) REFERENCES borrow (id_borrow)
        ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id_user, id_borrow)
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

INSERT INTO comment (id_product, id_user, mark, description) VALUES (1, 1, 1, "Trop grand, on dirait que Michmich l'a porté !");
INSERT INTO comment (id_product, id_user, mark) VALUES (3, 2, 4);

INSERT INTO product (id_product_type, name, image_url, price) VALUES (1, "T-shirt Star Wars", "https://images-na.ssl-images-amazon.com/images/I/61IpbkO3V8L._UX385_.jpg", 15);
INSERT INTO product (id_product_type, name, image_url, price) VALUES (2, "The Witcher tome 1", "https://images-na.ssl-images-amazon.com/images/I/51gbaCE0GPL._SX307_BO1,204,203,200_.jpg", 20);
INSERT INTO product (id_product_type, name, image_url, price) VALUES (4, "Bétonnière", "https://www.pointp.fr/asset/30/58/AST2243058-XL.jpg", 150);

INSERT INTO borrow (id_user, id_product, state, asking_date, borrowing_date) VALUES (3, 3, 1, datetime("2019-11-05 16:37"), datetime("2019-11-05 16:37"));
INSERT INTO borrow (id_user, id_product, state, asking_date) VALUES (2, 3, 0, datetime("2019-11-06 18:17"));


INSERT INTO customer (username, password) VALUES ("jeremy", "jeremy");
INSERT INTO customer (username, password) VALUES ("seb", "seb");

INSERT INTO chosen_product (id_customer, id_product) VALUES (1, 1);

INSERT INTO bank_account (id_customer, wallet, currency) VALUES (1, 100, "EUR");
INSERT INTO bank_account (id_customer, wallet, currency) VALUES (2, 150, "USD");
