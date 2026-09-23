const DatabaseFactory = require('../database/dbFactory');

let currentQuizIndex = 0;

async function getQuestion(req, res, next) {
  try {
    const db = await DatabaseFactory.getAdapter();

    // If POST payload sent to question endpoint
    if (req.method === 'POST' && (req.body.questionId || req.body.qid) && (req.body.rating || req.body.sid)) {
      await db.recordAnswer(req.user.email, req.body.questionId || req.body.qid, req.body.rating || req.body.sid);
      currentQuizIndex++;
      return res.status(200).json({ message: 'Answer recorded' });
    }

    const questions = await db.getQuestions();
    if (!questions || questions.length === 0) {
      return res.status(404).json({ error: 'No quiz questions found' });
    }

    const q = questions[currentQuizIndex % questions.length];
    return res.status(200).json({
      questionId: q.questionId || q.id,
      title: q.title,
      question: q.question,
      answer: q.answer
    });
  } catch (err) {
    next(err);
  }
}

async function submitAnswer(req, res, next) {
  try {
    const db = await DatabaseFactory.getAdapter();
    await db.recordAnswer(req.user.email, req.body.questionId || req.body.qid, req.body.rating || req.body.sid);
    currentQuizIndex++;
    return res.status(200).json({ message: 'Answer recorded' });
  } catch (err) {
    next(err);
  }
}

module.exports = {
  getQuestion,
  submitAnswer
};
