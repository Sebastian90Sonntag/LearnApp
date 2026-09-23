const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const DatabaseFactory = require('../database/dbFactory');

const uploadsDir = path.join(__dirname, '../uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

async function getProfile(req, res, next) {
  try {
    const db = await DatabaseFactory.getAdapter();
    const user = await db.findUserByEmail(req.user.email);

    return res.status(200).json({
      username: user ? user.username : req.user.username,
      email: req.user.email,
      image_link: user ? user.image_link || '' : ''
    });
  } catch (err) {
    next(err);
  }
}

async function uploadAvatar(req, res, next) {
  try {
    let imageBase64 = req.body.imageBase64 || req.body[crypto.createHash('md5').update('image').digest('hex')];

    const filename = `avatar_${Date.now()}.png`;
    const filepath = path.join(uploadsDir, filename);

    if (imageBase64) {
      fs.writeFileSync(filepath, Buffer.from(imageBase64, 'base64'));
    }

    const host = req.headers.host || `localhost:${process.env.PORT || 8080}`;
    const imageLink = `http://${host}/uploads/${filename}`;

    const db = await DatabaseFactory.getAdapter();
    await db.updateUserAvatar(req.user.email, imageLink);

    return res.status(200).json({ image_link: imageLink });
  } catch (err) {
    next(err);
  }
}

module.exports = {
  getProfile,
  uploadAvatar
};
