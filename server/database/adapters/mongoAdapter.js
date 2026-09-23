const { MongoClient } = require('mongodb');
const BaseAdapter = require('./baseAdapter');

class MongoAdapter extends BaseAdapter {
  constructor(config = {}) {
    super();
    this.uri = config.uri || process.env.MONGO_URI || 'mongodb://localhost:27017/learnapp';
    this.client = null;
    this.db = null;
  }

  async connect() {
    try {
      this.client = new MongoClient(this.uri);
      await this.client.connect();
      this.db = this.client.db();
      console.log(`[MongoDB] Connected successfully to ${this.uri}`);
      await this.initTables();
    } catch (err) {
      console.error('[MongoDB] Connection error:', err.message);
      throw err;
    }
  }

  async disconnect() {
    if (this.client) {
      await this.client.close();
      console.log('[MongoDB] Connection closed.');
    }
  }

  async initTables() {
    const usersColl = this.db.collection('users');
    await usersColl.createIndex({ email: 1 }, { unique: true });

    await this.seedInitialData();
  }

  async seedInitialData() {
    const questionsColl = this.db.collection('quiz_questions');
    const count = await questionsColl.countDocuments();
    if (count === 0) {
      const initialQuestions = [
        {
          id: '1',
          questionId: '1',
          title: 'Android Architecture',
          question: 'What pattern separates UI, business logic, and data handling using LiveData/State?',
          answer: 'MVVM (Model-View-ViewModel)'
        },
        {
          id: '2',
          questionId: '2',
          title: 'Security',
          question: 'What header standard is used to send JWT tokens in HTTP requests?',
          answer: 'Authorization: Bearer <token>'
        },
        {
          id: '3',
          questionId: '3',
          title: 'Java & Android',
          question: 'Which component executes background tasks tied to Lifecycle in MVVM?',
          answer: 'ViewModel'
        }
      ];
      await questionsColl.insertMany(initialQuestions);
    }
  }

  async findUserByEmail(email) {
    const usersColl = this.db.collection('users');
    const user = await usersColl.findOne({ email: email.toLowerCase() });
    return user || null;
  }

  async createUser(userData) {
    const usersColl = this.db.collection('users');
    const newUser = {
      email: userData.email.toLowerCase(),
      username: userData.username,
      salt: userData.salt,
      hash: userData.hash,
      image_link: userData.image_link || '',
      score: userData.score || 50,
      created_at: new Date()
    };

    const result = await usersColl.insertOne(newUser);
    return {
      id: result.insertedId,
      email: newUser.email,
      username: newUser.username,
      image_link: newUser.image_link,
      score: newUser.score
    };
  }

  async updateUserAvatar(email, imageLink) {
    const usersColl = this.db.collection('users');
    const result = await usersColl.updateOne(
      { email: email.toLowerCase() },
      { $set: { image_link: imageLink } }
    );
    return result.modifiedCount > 0;
  }

  async getScoreboard() {
    const usersColl = this.db.collection('users');
    const users = await usersColl.find().sort({ score: -1 }).toArray();
    return users.map(u => ({
      username: u.username,
      score: String(u.score || 0),
      image_link: u.image_link || ''
    }));
  }

  async getQuestions() {
    const questionsColl = this.db.collection('quiz_questions');
    const questions = await questionsColl.find().toArray();
    return questions.map(q => ({
      questionId: q.questionId || q.id,
      title: q.title,
      question: q.question,
      answer: q.answer
    }));
  }

  async recordAnswer(email, questionId, rating) {
    const usersColl = this.db.collection('users');
    await usersColl.updateOne(
      { email: email.toLowerCase() },
      { $inc: { score: 10 } }
    );
    return true;
  }
}

module.exports = MongoAdapter;
