const mysql = require('mysql2/promise');
const BaseAdapter = require('./baseAdapter');

class MySQLAdapter extends BaseAdapter {
  constructor(config = {}) {
    super();
    this.config = {
      host: config.host || process.env.MYSQL_HOST || 'localhost',
      port: parseInt(config.port || process.env.MYSQL_PORT || '3306', 10),
      user: config.user || process.env.MYSQL_USER || 'root',
      password: config.password || process.env.MYSQL_PASSWORD || 'root',
      database: config.database || process.env.MYSQL_DB || 'learnapp'
    };
    this.pool = null;
  }

  async connect() {
    try {
      this.pool = mysql.createPool({
        ...this.config,
        waitForConnections: true,
        connectionLimit: 10,
        queueLimit: 0
      });
      const connection = await this.pool.getConnection();
      console.log(`[MySQL] Connected successfully to ${this.config.host}:${this.config.port}/${this.config.database}`);
      connection.release();
      await this.initTables();
    } catch (err) {
      console.error('[MySQL] Connection error:', err.message);
      throw err;
    }
  }

  async disconnect() {
    if (this.pool) {
      await this.pool.end();
      console.log('[MySQL] Connection pool closed.');
    }
  }

  async initTables() {
    const createUsersTable = `
      CREATE TABLE IF NOT EXISTS users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        username VARCHAR(255) NOT NULL,
        salt TEXT NOT NULL,
        hash TEXT NOT NULL,
        image_link TEXT,
        score INT DEFAULT 0,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    `;

    const createQuestionsTable = `
      CREATE TABLE IF NOT EXISTS quiz_questions (
        id VARCHAR(50) PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
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
        `INSERT IGNORE INTO quiz_questions (id, title, question, answer) VALUES (?, ?, ?, ?)`,
        q
      );
    }
  }

  async findUserByEmail(email) {
    const [rows] = await this.pool.query('SELECT * FROM users WHERE LOWER(email) = LOWER(?)', [email]);
    return rows[0] || null;
  }

  async createUser(userData) {
    const query = `
      INSERT INTO users (email, username, salt, hash, image_link, score)
      VALUES (?, ?, ?, ?, ?, ?)
    `;
    const values = [
      userData.email.toLowerCase(),
      userData.username,
      userData.salt,
      userData.hash,
      userData.image_link || '',
      userData.score || 50
    ];

    const [result] = await this.pool.query(query, values);
    return {
      id: result.insertId,
      email: userData.email,
      username: userData.username,
      image_link: userData.image_link || '',
      score: userData.score || 50
    };
  }

  async updateUserAvatar(email, imageLink) {
    const [result] = await this.pool.query(
      'UPDATE users SET image_link = ? WHERE LOWER(email) = LOWER(?)',
      [imageLink, email]
    );
    return result.affectedRows > 0;
  }

  async getScoreboard() {
    const [rows] = await this.pool.query(
      'SELECT username, CAST(score AS CHAR) as score, image_link FROM users ORDER BY score DESC'
    );
    return rows;
  }

  async getQuestions() {
    const [rows] = await this.pool.query(
      'SELECT id as questionId, title, question, answer FROM quiz_questions'
    );
    return rows;
  }

  async recordAnswer(email, questionId, rating) {
    await this.pool.query(
      'UPDATE users SET score = score + 10 WHERE LOWER(email) = LOWER(?)',
      [email]
    );
    return true;
  }
}

module.exports = MySQLAdapter;
