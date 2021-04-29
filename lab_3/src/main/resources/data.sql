-- Populate Database with
-- -- 3 customers
-- -- 3 wallets (1 per customer)
-- -- 2 transaction (from Customer1 to Customer2, from Customer1 to Customer3)

-- Alice_password1
insert into user(id, username, password, email, is_enabled, roles) values(NEXTVAL(pk_sequence), "alice_in_wonderland",
                                                                          "{bcrypt}$2a$10$PBp/YvYi0NoYnG/erDcn4uHSREj0cYmDAgl4yWD86mnSPcFRg1NMe", "alice_inwonderland@mail.com",
                                                                          1, "CUSTOMER");

-- Hares_password1
insert into user(id, username, password, email, is_enabled, roles) values(NEXTVAL(pk_sequence), "the_march_hare",
                                                                          "{bcrypt}$2a$10$2LK2qJA8A85zLGZNEtAnQuy2l5ts5WlGvZiA/VO7iyEwFB8NE2QeS", "march_hare@mail.com",
                                                                          0, "CUSTOMER");

-- Hatters_password1
insert into user(id, username, password, email, is_enabled, roles) values(NEXTVAL(pk_sequence), "mad_hatter",
                                                                          "{bcrypt}$2a$10$ZpJLsDK9JDGtwGfVA0x79eaz.2Duue/9N2hFm0LaLbPDJJNGWdGgS", "mad_hatter@mail.com",
                                                                          0, "CUSTOMER");

insert into customer(id, name, surname, address, email, user) values(NEXTVAL(pk_sequence), "Alice", "Pleasance Liddell", "Wonderland, 2", "alice_inwonderland@mail.com", 1);
insert into customer(id, name, surname, address, email, user) values(NEXTVAL(pk_sequence), "The March", "Hare", "Dining Table, 1", "march_hare@mail.com", 2);
insert into customer(id, name, surname, address, email, user) values(NEXTVAL(pk_sequence), "The Mad", "Hatter", "Dining Table, 2", "mad_hatter@mail.com", 3);
insert into wallet(id, balance, customer) values(NEXTVAL(pk_sequence), 1000, 4);
insert into wallet(id, balance, customer) values(NEXTVAL(pk_sequence), 750, 5);
insert into wallet(id, balance, customer) values(NEXTVAL(pk_sequence), 500, 6);
insert into transaction(id, amount, timestamp, sender, receiver) values (NEXTVAL(pk_sequence), 50, "2020-01-01 00:00:00", 7, 8);
insert into transaction(id, amount, timestamp, sender, receiver) values (NEXTVAL(pk_sequence), 250, "2020-01-01 00:00:00", 7, 9);