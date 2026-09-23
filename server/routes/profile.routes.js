const express = require('express');
const router = express.Router();
const profileController = require('../controllers/profile.controller');
const { authenticateToken } = require('../middlewares/auth.middleware');

router.use(authenticateToken);

router.get('/', profileController.getProfile);
router.post('/avatar', profileController.uploadAvatar);

module.exports = router;
