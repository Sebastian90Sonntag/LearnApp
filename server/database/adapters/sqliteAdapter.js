const sqlite3 = require('sqlite3').verbose();
const path = require('path');
const BaseAdapter = require('./baseAdapter');

class SQLiteAdapter extends BaseAdapter {
  constructor(config = {}) {
    super();
    const dbPath = config.dbFile || process.env.DB_FILE || path.join(__dirname, '../../database.sqlite');
    this.dbPath = path.resolve(dbPath);
    this.db = null;
  }

  async connect() {
    return new Promise((resolve, reject) => {
      this.db = new sqlite3.Database(this.dbPath, (err) => {
        if (err) {
          console.error('[SQLite] Connection error:', err.message);
          return reject(err);
        }
        console.log(`[SQLite] Connected to local database file: ${this.dbPath}`);
        this.initTables()
          .then(resolve)
          .catch(reject);
      });
    });
  }

  async disconnect() {
    return new Promise((resolve, reject) => {
      if (this.db) {
        this.db.close((err) => {
          if (err) return reject(err);
          console.log('[SQLite] Database connection closed.');
          resolve();
        });
      } else {
        resolve();
      }
    });
  }

  async initTables() {
    const createUsersTable = `
      CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        email TEXT UNIQUE NOT NULL,
        username TEXT NOT NULL,
        salt TEXT NOT NULL,
        hash TEXT NOT NULL,
        image_link TEXT DEFAULT '',
        score INTEGER DEFAULT 0,
        created_at DATETIME DEFAULT CURRENT_TIMESTAMP
      );
    `;

    const createQuestionsTable = `
      CREATE TABLE IF NOT EXISTS quiz_questions (
        id TEXT PRIMARY KEY,
        title TEXT NOT NULL,
        question TEXT NOT NULL,
        answer TEXT NOT NULL
      );
    `;

    return new Promise((resolve, reject) => {
      this.db.serialize(() => {
        this.db.run(createUsersTable);
        this.db.run(createQuestionsTable, (err) => {
          if (err) return reject(err);
          this.seedInitialData()
            .then(resolve)
            .catch(reject);
        });
      });
    });
  }

  async seedInitialData() {
    return new Promise((resolve, reject) => {
      const initialQuestions = [
        {
          id: '1',
          title: 'Android Architecture',
          question: 'What pattern separates UI, business logic, and data handling using LiveData/State?',
          answer: 'MVVM (Model-View-ViewModel)'
        },
        {
          id: '2',
          title: 'Security',
          question: 'What header standard is used to send JWT tokens in HTTP requests?',
          answer: 'Authorization: Bearer <token>'
        },
        {
          id: '3',
          title: 'Java & Android',
          question: 'Which component executes background tasks tied to Lifecycle in MVVM?',
          answer: 'ViewModel'
        }
      ];

      const stmt = this.db.prepare(
        `INSERT OR IGNORE INTO quiz_questions (id, title, question, answer) VALUES (?, ?, ?, ?)`
      );

      for (const q of initialQuestions) {
        stmt.run(q.id, q.title, q.question, q.answer);
      }

      stmt.finalize((err) => {
        if (err) return reject(err);
        resolve();
      });
    });
  }

  async findUserByEmail(email) {
    return new Promise((resolve, reject) => {
      const query = `SELECT * FROM users WHERE LOWER(email) = LOWER(?)`;
      this.db.get(query, [email], (err, row) => {
        if (err) return reject(err);
        resolve(row || null);
      });
    });
  }

  async createUser(userData) {
    return new Promise((resolve, reject) => {
      const query = `
        INSERT INTO users (email, username, salt, hash, image_link, score)
        VALUES (?, ?, ?, ?, ?, ?)
      `;
      const params = [
        userData.email.toLowerCase(),
        userData.username,
        userData.salt,
        userData.hash,
        userData.image_link || '',
        userData.score || 50
      ];

      this.db.run(query, params, function (err) {
        if (err) return reject(err);
        resolve({
          id: this.lastID,
          email: userData.email,
          username: userData.username,
          image_link: userData.image_link || '',
          score: userData.score || 50
        });
      });
    });
  }

  async updateUserAvatar(email, imageLink) {
    return new Promise((resolve, reject) => {
      const query = `UPDATE users SET image_link = ? WHERE LOWER(email) = LOWER(?)`;
      this.db.run(query, [imageLink, email], function (err) {
        if (err) return reject(err);
        resolve(this.changes > 0);
      });
    });
  }

  async getScoreboard() {
    return new Promise((resolve, reject) => {
      const query = `SELECT username, CAST(score AS TEXT) as score, image_link FROM users ORDER BY score DESC`;
      this.db.all(query, [], (err, rows) => {
        if (err) return reject(err);
        resolve(rows || []);
      });
    });
  }

  async getQuestions() {
    return new Promise((resolve, reject) => {
      const query = `SELECT id as questionId, title, question, answer FROM quiz_questions`;
      this.db.all(query, [], (err, rows) => {
        if (err) return reject(err);
        resolve(rows || []);
      });
    });
  }

  async recordAnswer(email, questionId, rating) {
    return new Promise((resolve, reject) => {
      const increment = 10;
      const query = `UPDATE users SET score = score + ? WHERE LOWER(email) = LOWER(?)`;
      this.db.run(query, [increment, email], function (err) {
        if (err) return reject(err);
        resolve(true);
      });
    });
  }
}

module.exports = SQLiteAdapter;
