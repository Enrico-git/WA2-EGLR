-- Populate Database with
-- -- 3 customers
-- -- 3 wallets (1 per customer)
-- -- 2 transaction (from Customer1 to Customer2, from Customer1 to Customer3)
insert into customer(id, name, surname, address, email) values(1, "Alice", "Pleasance Liddell", "Wonderland, 2", "alice_inwonderland@mail.com");
insert into customer(id, name, surname, address, email) values(2, "The March", "Hare", "Dining Table, 1", "march_hare@mail.com");
insert into customer(id, name, surname, address, email) values(3, "The Mad", "Hatter", "Dining Table, 2", "mad_hatter@mail.com");
insert into wallet(id, balance, customer) values(4, 1000, 1);
insert into wallet(id, balance, customer) values(5, 750, 2);
insert into wallet(id, balance, customer) values(6, 500, 3);
insert into transaction(id, amount, timestamp, sender, receiver) values (7, 50, "2020-01-01 00:00:00", 4, 5);
insert into transaction(id, amount, timestamp, sender, receiver) values (8, 250, "2020-01-01 00:00:00", 4, 6);

update pk_sequence set next_val = 9;