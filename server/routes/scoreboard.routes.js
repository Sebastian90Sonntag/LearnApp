const express = require('express');
const router = express.Router();
const scoreboardController = require('../controllers/scoreboard.controller');
const { authenticateToken } = require('../middlewares/auth.middleware');

router.use(authenticateToken);

router.get('/', scoreboardController.getScoreboard);
router.post('/', scoreboardController.getScoreboard);

module.exports = router;
