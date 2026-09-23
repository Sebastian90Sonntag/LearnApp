const express = require('express');
const router = express.Router();

const authRoutes = require('./auth.routes');
const quizRoutes = require('./quiz.routes');
const scoreboardRoutes = require('./scoreboard.routes');
const profileRoutes = require('./profile.routes');
const authController = require('../controllers/auth.controller');
const profileController = require('../controllers/profile.controller');
const { authenticateToken } = require('../middlewares/auth.middleware');

// Version 1 REST API Routes
router.use('/api/v1/auth', authRoutes);
router.use('/api/v1/quiz', quizRoutes);
router.use('/api/v1/scoreboard', scoreboardRoutes);
router.use('/api/v1/profile', profileRoutes);

// Legacy Route Endpoints for Backward Compatibility
router.post('/login', authController.login);
router.post('/register', authController.register);
router.use('/quiz', quizRoutes);
router.use('/scoreboard', scoreboardRoutes);
router.use('/profile', profileRoutes);
router.post('/profile/', authenticateToken, profileController.uploadAvatar);

module.exports = router;
