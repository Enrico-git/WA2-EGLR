conn = new Mongo();
db = conn.getDB("catalogservice");
// password: Valid_password2
db.users.insertOne({ "_id" : ObjectId("612201df349bb2786821c881"), "username" : "myUserName",
 "password" : "{bcrypt}$2a$10$0guO/FPK70EVSE1XLdlfkuFfjZWErCvxznIYQsVldu9HcpFoHE5pa",
 "email" : "eglrgroup9@gmail.com", "isEnabled" : true,
 "roles" : "CUSTOMER", "version" : NumberLong(1), "_class" : "it.polito.wa2.catalogservice.domain.User" });

db.users.insertOne({ "_id" : ObjectId("612201df349bb2786821c882"), "username" : "myUserNameAdmin", 
 "password" : "{bcrypt}$2a$10$0guO/FPK70EVSE1XLdlfkuFfjZWErCvxznIYQsVldu9HcpFoHE5pa", 
 "email" : "ailybkjdjtnjjalybh@bptfp.net", "isEnabled" : true, 
 "roles" : "ADMIN", "version" : NumberLong(1), "_class" : "it.polito.wa2.catalogservice.domain.User" });

db.customers.insertOne(
{ "_id" : ObjectId("61220258349bb2786821c883"),
 "name" : "John", "surname" : "Doe",
 "address" : "Wa2 Street",
 "email" : "eglrgroup9@gmail.com",
 "user" : ObjectId("612201df349bb2786821c881"),
 "_class" : "it.polito.wa2.catalogservice.domain.Customer" }
);

db.customers.insertOne( 
{ "_id" : ObjectId("61220258349bb2786821c887"),
 "name" : "John the ADMIN", "surname" : "Doe",
 "address" : "Wa2 Street",
 "email" : "ailybkjdjtnjjalybh@bptfp.net",
 "user" : ObjectId("612201df349bb2786821c882"), 
 "_class" : "it.polito.wa2.catalogservice.domain.Customer" }
);


db = conn.getDB("walletservice");

db.wallets.insertOne({ "_id" : ObjectId("6134871711dbe16c02850167"), 
"balance" : "12000", 
"userID" : ObjectId("612201df349bb2786821c881"), 
"version" : NumberLong(1), 
"_class" : "it.polito.wa2.walletservice.entities.Wallet" });

db = conn.getDB("warehouseservice");

db.products.insertMany([{ "_id" : ObjectId("613488c9971c0b138c7eb653"),
 "name" : "Coca Cola",
 "description" : "A good drink",
 "pictureUrl" : "CocaColaUrl",
 "category" : "Drink",
 "price" : "0.99",
 "avgRating" : 0,
 "creationDate" : ISODate("2021-09-05T09:07:21.463Z"),
 "comments" : [ ],
 "version" : NumberLong(0),
 "_class" : "it.polito.wa2.warehouseservice.domain.Product" },
{ "_id" : ObjectId("613488c9971c0b138c7eb654"),
 "name" : "Razer Diamondback 3G", 
 "description" : "Gaming mouse", 
 "pictureUrl" : "the picture", 
 "category" : "Technology", 
 "price" : "35", 
 "avgRating" : 0, 
 "creationDate" : ISODate("2021-09-05T09:07:21.463Z"), 
 "comments" : [ ], 
 "version" : NumberLong(0), 
 "_class" : "it.polito.wa2.warehouseservice.domain.Product" },
{ "_id" : ObjectId("613488c9971c0b138c7eb655"),
 "name" : "Alice and what she found behind the glass", 
 "description" : "A nice book", 
 "pictureUrl" : "Alices selfie", 
 "category" : "Books", 
 "price" : "8", 
 "avgRating" : 0, 
 "creationDate" : ISODate("2021-09-05T09:07:21.463Z"), 
 "comments" : [ ], 
 "version" : NumberLong(0), 
 "_class" : "it.polito.wa2.warehouseservice.domain.Product" }]);

db.warehouses.insertMany([
{ "_id" : ObjectId("61348a58971c0b138c7eb654"), 
"products" : [ { "productId" : ObjectId("613488c9971c0b138c7eb653"),
 "alarm" : 2, "quantity" : 100 },
{ "productId" : ObjectId("613488c9971c0b138c7eb654"), 
 "alarm" : 1, "quantity" : 20 },
{ "productId" : ObjectId("613488c9971c0b138c7eb655"), 
 "alarm" : 2, "quantity" : 100 }
 ], "version" : NumberLong(0), 
"_class" : "it.polito.wa2.warehouseservice.domain.Warehouse" 
},
{ "_id" : ObjectId("61348a58971c0b138c7eb655"), 
"products" : [ { "productId" : ObjectId("613488c9971c0b138c7eb653"), 
 "alarm" : 2, "quantity" : 10 },
{ "productId" : ObjectId("613488c9971c0b138c7eb655"), 
 "alarm" : 1, "quantity" : 5 }
 ], "version" : NumberLong(0), 
"_class" : "it.polito.wa2.warehouseservice.domain.Warehouse"
}]);
