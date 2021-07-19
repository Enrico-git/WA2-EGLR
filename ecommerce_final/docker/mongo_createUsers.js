conn = new Mongo();
db = conn.getDB("admin");
//db.auth("root", "root");
//db = conn.getDB("orderservice");
db.createUser({
  user: 'debezium',
  pwd: 'debezium',
  roles: [
    {
      role: 'read',
      db: 'orderservice',
    },
    {
      role: 'read',
      db: 'walletservice',
    },
    {
      role: 'read',
      db: 'warehouseservice',
    }
  ],
  passwordDigestor : 'server'
});

db.createUser({
  user: 'orderservice',
  pwd: 'orderservice',
  roles: [
    {
      role: 'readWrite',
      db: 'orderservice',
    },
    {
      role: 'read',
      db: 'users',
    }
  ],
  passwordDigestor : 'server'
});

db.createUser({
  user: 'catalogservice',
  pwd: 'catalogservice',
  roles: [
    {
      role: 'readWrite',
      db: 'users',
    }
  ],
  passwordDigestor : 'server'
});

db.createUser({
  user: 'walletservice',
  pwd: 'walletservice',
  roles: [
    {
      role: 'readWrite',
      db: 'walletservice',
    },
    {
      role: 'read',
      db: 'users',
    }
  ],
  passwordDigestor : 'server'
});

db.createUser({
  user: 'warehouseservice',
  pwd: 'warehouseservice',
  roles: [
    {
      role: 'readWrite',
      db: 'warehouseservice',
    }
  ],
  passwordDigestor : 'server'
});
