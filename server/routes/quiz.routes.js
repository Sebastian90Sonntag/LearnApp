const express = require('express');
const router = express.Router();
const quizController = require('../controllers/quiz.controller');
const { authenticateToken } = require('../middlewares/auth.middleware');

router.use(authenticateToken);

router.get('/question', quizController.getQuestion);
router.post('/question', quizController.getQuestion);
router.post('/answer', quizController.submitAnswer);

module.exports = router;
