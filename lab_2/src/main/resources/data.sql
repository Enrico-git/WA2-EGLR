-- Populate Database with
-- -- 3 customers
-- -- 3 wallets (1 per customer)
-- -- 2 transaction (from Customer1 to Customer2, from Customer1 to Customer3)
insert into customer(id, name, surname, address, email) values(NEXTVAL(pk_sequence), "Alice", "Pleasance Liddell", "Wonderland, 2", "alice_inwonderland@mail.com");
insert into customer(id, name, surname, address, email) values(NEXTVAL(pk_sequence), "The March", "Hare", "Dining Table, 1", "march_hare@mail.com");
insert into customer(id, name, surname, address, email) values(NEXTVAL(pk_sequence), "The Mad", "Hatter", "Dining Table, 2", "mad_hatter@mail.com");
insert into wallet(id, balance, customer) values(NEXTVAL(pk_sequence), 1000, 1);
insert into wallet(id, balance, customer) values(NEXTVAL(pk_sequence), 750, 2);
insert into wallet(id, balance, customer) values(NEXTVAL(pk_sequence), 500, 3);
insert into transaction(id, amount, timestamp, sender, receiver) values (NEXTVAL(pk_sequence), 50, "2020-01-01 00:00:00", 4, 5);
insert into transaction(id, amount, timestamp, sender, receiver) values (NEXTVAL(pk_sequence), 250, "2020-01-01 00:00:00", 4, 6);
