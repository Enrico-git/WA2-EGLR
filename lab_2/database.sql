use ecommerce;

DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS customer;


CREATE TABLE customer(
     id BIGINT auto_increment,
     name VARCHAR(100) NOT NULL,
     surname VARCHAR(100) NOT NULL,
     address VARCHAR(255) NOT NULL,
     email VARCHAR(100) NOT NULL UNIQUE,
     primary key (id)
);

CREATE TABLE wallet(
   id BIGINT auto_increment,
   balance DECIMAL(15,2) UNSIGNED DEFAULT 0 NOT NULL,
   customer BIGINT NOT NULL,
   CONSTRAINT fk_wallet_customer
       FOREIGN KEY (customer) REFERENCES customer(id),
   primary key (id)
);

CREATE TABLE transaction(
    id BIGINT auto_increment,
    timestamp TIMESTAMP NOT NULL,
    sender BIGINT NOT NULL,
    receiver BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_trans_wallet_sender
        FOREIGN KEY (sender) REFERENCES wallet(id),
    CONSTRAINT fk_trans_wallet_receiver
        FOREIGN KEY (receiver) REFERENCES wallet(id),
    primary key (id)
);


-- Populate Database with
-- -- 3 customers
-- -- 3 wallets (1 per customer)
-- -- 2 transaction (from Customer1 to Customer2, from Customer1 to Customer3)
insert into customer(id, name, surname, address, email) values(1, "Alice", "Pleasance Liddell", "Wonderland, 2", "alice_inwonderland@mail.com");
insert into customer(id, name, surname, address, email) values(2, "The March", "Hare", "Dining Table, 1", "march_hare@mail.com");
insert into customer(id, name, surname, address, email) values(3, "The Mad", "Hatter", "Dining Table, 2", "mad_hatter@mail.com");
insert into wallet(id, balance, customer) values(1, 1000, 1);
insert into wallet(id, balance, customer) values(2, 750, 2);
insert into wallet(id, balance, customer) values(3, 500, 3);
insert into transaction(id, amount, timestamp, sender, receiver) values (1, 50, "2020-01-01 00:00:00", 1, 2);
insert into transaction(id, amount, timestamp, sender, receiver) values (2, 250, "2020-01-01 00:00:00", 1, 3);
