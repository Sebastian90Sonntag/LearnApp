const bcrypt = require('bcryptjs');
const crypto = require('crypto');

// Generate salt and hash using crypto PBKDF2 (compatible with original server.js)
function hashPassword(password, salt = crypto.randomBytes(16).toString('hex')) {
  const hash = crypto.pbkdf2Sync(password, salt, 1000, 64, 'sha512').toString('hex');
  return { salt, hash };
}

function verifyPassword(password, salt, storedHash) {
  if (salt && storedHash) {
    const hash = crypto.pbkdf2Sync(password, salt, 1000, 64, 'sha512').toString('hex');
    return hash === storedHash;
  }
  return false;
}

module.exports = {
  hashPassword,
  verifyPassword
};
