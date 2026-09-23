const express = require('express');
const router = express.Router();
const authController = require('../controllers/auth.controller');

// Main REST routes
router.post('/login', authController.login);
router.post('/register', authController.register);
router.post('/forgot-password', authController.forgotPassword);
router.post('/reset-password', authController.resetPassword);

module.exports = router;
