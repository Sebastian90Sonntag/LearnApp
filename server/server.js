require('dotenv').config();
const http = require('http');
const app = require('./app');
const DatabaseFactory = require('./database/dbFactory');

const PORT = process.env.PORT || 8080;

async function startServer() {
  try {
    // Initialize Database System Connector based on DB_TYPE
    const db = await DatabaseFactory.getAdapter();

    const server = http.createServer(app);

    server.listen(PORT, '0.0.0.0', () => {
      console.log(`[LearnApp Backend] Server running on http://0.0.0.0:${PORT}`);
      console.log(`[LearnApp Backend] Database Engine Active: ${process.env.DB_TYPE || 'sqlite'}`);
    });

    const shutdown = async (signal) => {
      console.log(`\nReceived ${signal}. Shutting down gracefully...`);
      server.close(async () => {
        console.log('HTTP server closed.');
        try {
          await db.disconnect();
        } catch (err) {
          console.error('Error during DB disconnect:', err.message);
        }
        process.exit(0);
      });
    };

    process.on('SIGINT', () => shutdown('SIGINT'));
    process.on('SIGTERM', () => shutdown('SIGTERM'));

  } catch (err) {
    console.error('Fatal error starting server:', err.message);
    process.exit(1);
  }
}

startServer();
