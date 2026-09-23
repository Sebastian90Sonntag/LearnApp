const { verifyJwt } = require('../services/jwt.service');
const DatabaseFactory = require('../database/dbFactory');

async function authenticateToken(req, res, next) {
  let token = null;

  // 1. Extract Bearer token from Authorization header
  const authHeader = req.headers['authorization'];
  if (authHeader && authHeader.startsWith('Bearer ')) {
    token = authHeader.substring(7).trim();
  }

  // 2. Fallback to token parameter in request body or query string
  if (!token && req.body) {
    token = req.body.token || req.body.t;
  }
  if (!token && req.query) {
    token = req.query.token || req.query.t;
  }

  if (!token) {
    return res.status(401).json({ error: 'Unauthorized: Missing JWT token' });
  }

  const decoded = verifyJwt(token);
  if (!decoded) {
    return res.status(401).json({ error: 'Unauthorized: Invalid or expired JWT token' });
  }

  try {
    const db = await DatabaseFactory.getAdapter();
    const user = await db.findUserByEmail(decoded.email);
    req.user = user || { email: decoded.email, username: decoded.username };
    next();
  } catch (err) {
    next(err);
  }
}

module.exports = {
  authenticateToken
};
