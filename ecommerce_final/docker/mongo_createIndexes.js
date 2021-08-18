conn = new Mongo();
db = conn.getDB("catalogservice");
db.customers.createIndex({"user": 1}, {unique: true});
db.customers.createIndex({"email": 1}, {unique: true});
db.users.createIndex({"username": 1}, {unique: true});
db.users.createIndex({"email": 1}, {unique: true});
db = conn.getDB("walletservice");
db.wallets.createIndex({"userID": 1}, {unique: true});

