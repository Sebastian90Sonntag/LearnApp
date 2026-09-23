const { Pool } = require('pg');
const BaseAdapter = require('./baseAdapter');

class PostgresAdapter extends BaseAdapter {
  constructor(config = {}) {
    super();
    this.config = {
      host: config.host || process.env.POSTGRES_HOST || 'localhost',
      port: parseInt(config.port || process.env.POSTGRES_PORT || '5432', 10),
      user: config.user || process.env.POSTGRES_USER || 'postgres',
      password: config.password || process.env.POSTGRES_PASSWORD || 'postgres',
      database: config.database || process.env.POSTGRES_DB || 'learnapp'
    };
    this.pool = null;
  }

  async connect() {
    this.pool = new Pool(this.config);
    try {
      const client = await this.pool.connect();
      console.log(`[PostgreSQL] Connected successfully to ${this.config.host}:${this.config.port}/${this.config.database}`);
      client.release();
      await this.initTables();
    } catch (err) {
      console.error('[PostgreSQL] Connection error:', err.message);
      throw err;
    }
  }

  async disconnect() {
    if (this.pool) {
      await this.pool.end();
      console.log('[PostgreSQL] Connection pool closed.');
    }
  }

  async initTables() {
    const createUsersTable = `
      CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        username VARCHAR(255) NOT NULL,
        salt TEXT NOT NULL,
        hash TEXT NOT NULL,
        image_link TEXT DEFAULT '',
        score INTEGER DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    `;

    const createQuestionsTable = `
      CREATE TABLE IF NOT EXISTS quiz_questions (
        id VARCHAR(50) PRIMARY KEY,
        title TEXT NOT NULL,
        question TEXT NOT NULL,
        answer TEXT NOT NULL
      );
    `;

    await this.pool.query(createUsersTable);
    await this.pool.query(createQuestionsTable);
    await this.seedInitialData();
  }

  async seedInitialData() {
    const initialQuestions = [
      ['1', 'Android Architecture', 'What pattern separates UI, business logic, and data handling using LiveData/State?', 'MVVM (Model-View-ViewModel)'],
      ['2', 'Security', 'What header standard is used to send JWT tokens in HTTP requests?', 'Authorization: Bearer <token>'],
      ['3', 'Java & Android', 'Which component executes background tasks tied to Lifecycle in MVVM?', 'ViewModel']
    ];

    for (const q of initialQuestions) {
      await this.pool.query(
        `INSERT INTO quiz_questions (id, title, question, answer)
         VALUES ($1, $2, $3, $4)
         ON CONFLICT (id) DO NOTHING`,
        q
      );
    }
  }

  async findUserByEmail(email) {
    const res = await this.pool.query('SELECT * FROM users WHERE LOWER(email) = LOWER($1)', [email]);
    return res.rows[0] || null;
  }

  async createUser(userData) {
    const query = `
      INSERT INTO users (email, username, salt, hash, image_link, score)
      VALUES ($1, $2, $3, $4, $5, $6)
      RETURNING id, email, username, image_link, score
    `;
    const values = [
      userData.email.toLowerCase(),
      userData.username,
      userData.salt,
      userData.hash,
      userData.image_link || '',
      userData.score || 50
    ];

    const res = await this.pool.query(query, values);
    return res.rows[0];
  }

  async updateUserAvatar(email, imageLink) {
    const res = await this.pool.query(
      'UPDATE users SET image_link = $1 WHERE LOWER(email) = LOWER($2)',
      [imageLink, email]
    );
    return res.rowCount > 0;
  }

  async getScoreboard() {
    const res = await this.pool.query(
      'SELECT username, CAST(score AS VARCHAR) as score, image_link FROM users ORDER BY score DESC'
    );
    return res.rows;
  }

  async getQuestions() {
    const res = await this.pool.query(
      'SELECT id as "questionId", title, question, answer FROM quiz_questions'
    );
    return res.rows;
  }

  async recordAnswer(email, questionId, rating) {
    await this.pool.query(
      'UPDATE users SET score = score + 10 WHERE LOWER(email) = LOWER($1)',
      [email]
    );
    return true;
  }
}

module.exports = PostgresAdapter;
