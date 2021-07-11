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
});
