/**
 * Base Abstract Database Adapter Interface
 * All database connectors (SQLite, Postgres, MySQL, MongoDB) implement this contract.
 */
class BaseAdapter {
  async connect() {
    throw new Error('Method connect() must be implemented.');
  }

  async disconnect() {
    throw new Error('Method disconnect() must be implemented.');
  }

  async findUserByEmail(email) {
    throw new Error('Method findUserByEmail() must be implemented.');
  }

  async createUser(userData) {
    throw new Error('Method createUser() must be implemented.');
  }

  async updateUserAvatar(email, imageLink) {
    throw new Error('Method updateUserAvatar() must be implemented.');
  }

  async getScoreboard() {
    throw new Error('Method getScoreboard() must be implemented.');
  }

  async getQuestions() {
    throw new Error('Method getQuestions() must be implemented.');
  }

  async recordAnswer(email, questionId, rating) {
    throw new Error('Method recordAnswer() must be implemented.');
  }
}

module.exports = BaseAdapter;
