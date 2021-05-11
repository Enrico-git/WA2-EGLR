-- USE 1 AS VERSION OR DOOMSDAY HAPPENS
-- BUG OF SPRING IN REPO.SAVE

insert into product
values (NEXTVAL(pk_sequence), "Bread", "Food", 1.50, 10, 1);
    insert
into product
values (NEXTVAL(pk_sequence), "Apple", "Food", 1, 10, 1);
insert into product
values (NEXTVAL(pk_sequence), "Carrot", "Food", 1, 10, 1);
insert into product
values (NEXTVAL(pk_sequence), "MELANZANE", "Food", 2, 10, 1);