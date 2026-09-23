const jwt = require('jsonwebtoken');

const JWT_SECRET = process.env.JWT_SECRET || 'learnapp_super_secret_jwt_key_2026';
const EXPIRES_IN = '24h';

function generateJwt(payload) {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: EXPIRES_IN });
}

function verifyJwt(token) {
  if (!token) return null;
  try {
    return jwt.verify(token, JWT_SECRET);
  } catch (err) {
    return null;
  }
}

module.exports = {
  generateJwt,
  verifyJwt
};
