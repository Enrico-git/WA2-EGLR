DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS wallet;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS pk_sequence;

-- CREATE SEQUENCE pk_sequence START WITH 1 INCREMENT BY 1;
create table pk_sequence (next_val bigint) engine=InnoDB;
insert into pk_sequence values ( 1 );

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
