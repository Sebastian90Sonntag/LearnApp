const SQLiteAdapter = require('./adapters/sqliteAdapter');
const PostgresAdapter = require('./adapters/postgresAdapter');
const MySQLAdapter = require('./adapters/mysqlAdapter');
const MongoAdapter = require('./adapters/mongoAdapter');

class DatabaseFactory {
  static activeAdapter = null;

  static async getAdapter() {
    if (this.activeAdapter) {
      return this.activeAdapter;
    }

    const dbType = (process.env.DB_TYPE || 'sqlite').toLowerCase();
    console.log(`[DatabaseFactory] Initializing database connector for type: '${dbType}'`);

    switch (dbType) {
      case 'sqlite':
        this.activeAdapter = new SQLiteAdapter();
        break;
      case 'postgres':
      case 'postgresql':
        this.activeAdapter = new PostgresAdapter();
        break;
      case 'mysql':
        this.activeAdapter = new MySQLAdapter();
        break;
      case 'mongo':
      case 'mongodb':
        this.activeAdapter = new MongoAdapter();
        break;
      default:
        console.warn(`[DatabaseFactory] Unknown DB_TYPE '${dbType}', defaulting to SQLite.`);
        this.activeAdapter = new SQLiteAdapter();
        break;
    }

    await this.activeAdapter.connect();
    return this.activeAdapter;
  }
}

module.exports = DatabaseFactory;
