use ecommerce;

CREATE TABLE wallet(
   id INTEGER auto_increment,
   balance DOUBLE UNSIGNED DEFAULT 0 NOT NULL,
   customer INTEGER NOT NULL,
   CONSTRAINT fk_wallet_customer
       FOREIGN KEY (customer) REFERENCES customer(id),
   primary key (id)
);

CREATE TABLE transaction(
     id INTEGER auto_increment,
     timestamp TIMESTAMP NOT NULL,
     sender INTEGER NOT NULL,
     receiver INTEGER NOT NULL,
     amount DOUBLE NOT NULL,
     CONSTRAINT fk_trans_wallet_sender
         FOREIGN KEY (sender) REFERENCES wallet(id),
     CONSTRAINT fk_trans_wallet_receiver
         FOREIGN KEY (receiver) REFERENCES wallet(id),
     primary key (id)
);

CREATE TABLE customer(
     id INTEGER auto_increment,
     name VARCHAR(100) NOT NULL,
     surname VARCHAR(100) NOT NULL,
     address VARCHAR(255) NOT NULL,
     email VARCHAR(100) NOT NULL UNIQUE,
     primary key (id)
);

insert into customer values(1, "Jon", "Doe", "Test street 15", "jon_doe@mail.com");
insert into customer values(2, "Alice", "InWonderland", "Rabbit hole", "alice_wonderland@mail.com");
insert into wallet values(1, 50, 1);
insert into wallet values(2, 100, 2);
insert into transaction values (1, 50, "2020-01-01 00:00:00", 1, 2);