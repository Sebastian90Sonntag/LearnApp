const http = require('http');
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = process.env.PORT || 8080;
const JWT_SECRET = process.env.JWT_SECRET || 'learnapp_super_secret_jwt_key_2026';
const uploadsDir = path.join(__dirname, 'uploads');

if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

// Helper: Base64Url Encoding/Decoding for JWT
function base64UrlEncode(str) {
  return Buffer.from(str)
    .toString('base64')
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');
}

function base64UrlDecode(str) {
  str = str.replace(/-/g, '+').replace(/_/g, '/');
  while (str.length % 4) {
    str += '=';
  }
  return Buffer.from(str, 'base64').toString();
}

// Generate JWT Token
function generateJwt(payload) {
  const header = { alg: 'HS256', typ: 'JWT' };
  const encodedHeader = base64UrlEncode(JSON.stringify(header));
  
  // Add expiration (24h)
  const tokenPayload = {
    ...payload,
    iat: Math.floor(Date.now() / 1000),
    exp: Math.floor(Date.now() / 1000) + (24 * 60 * 60)
  };
  const encodedPayload = base64UrlEncode(JSON.stringify(tokenPayload));
  
  const signatureInput = `${encodedHeader}.${encodedPayload}`;
  const signature = crypto
    .createHmac('sha256', JWT_SECRET)
    .update(signatureInput)
    .digest('base64')
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');
    
  return `${signatureInput}.${signature}`;
}

// Verify JWT Token
function verifyJwt(token) {
  if (!token) return null;
  const parts = token.split('.');
  if (parts.length !== 3) return null;
  
  const [encodedHeader, encodedPayload, signature] = parts;
  const signatureInput = `${encodedHeader}.${encodedPayload}`;
  const expectedSignature = crypto
    .createHmac('sha256', JWT_SECRET)
    .update(signatureInput)
    .digest('base64')
    .replace(/=/g, '')
    .replace(/\+/g, '-')
    .replace(/\//g, '_');
    
  if (signature !== expectedSignature) return null;
  
  try {
    const payload = JSON.parse(base64UrlDecode(encodedPayload));
    if (payload.exp && Math.floor(Date.now() / 1000) > payload.exp) {
      return null; // Expired
    }
    return payload;
  } catch (err) {
    return null;
  }
}

// Password Hashing (PBKDF2 with Salt)
function hashPassword(password, salt = crypto.randomBytes(16).toString('hex')) {
  const hash = crypto.pbkdf2Sync(password, salt, 1000, 64, 'sha512').toString('hex');
  return { salt, hash };
}

function verifyPassword(password, salt, storedHash) {
  const hash = crypto.pbkdf2Sync(password, salt, 1000, 64, 'sha512').toString('hex');
  return hash === storedHash;
}

// In-memory User Data Store
const users = new Map();

// Seed initial demo user
const demoPassword = hashPassword('Password123!');
users.set('demo@example.com', {
  email: 'demo@example.com',
  salt: demoPassword.salt,
  hash: demoPassword.hash,
  username: 'DemoUser',
  image_link: '',
  score: 100
});

const quizQuestions = [
  {
    id: '1',
    title: 'Android Architecture',
    question: 'What pattern separates UI, business logic, and data handling using LiveData/State?',
    answer: 'MVVM (Model-View-ViewModel)'
  },
  {
    id: '2',
    title: 'Security',
    question: 'What header standard is used to send JWT tokens in HTTP requests?',
    answer: 'Authorization: Bearer <token>'
  },
  {
    id: '3',
    title: 'Java & Android',
    question: 'Which component executes background tasks tied to Lifecycle in MVVM?',
    answer: 'ViewModel'
  }
];

let currentQuizIndex = 0;

function sendJson(res, statusCode, data) {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  });
  res.end(JSON.stringify(data));
}

function extractBearerToken(req) {
  const authHeader = req.headers['authorization'];
  if (authHeader && authHeader.startsWith('Bearer ')) {
    return authHeader.substring(7).trim();
  }
  return null;
}

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    return res.end();
  }

  // Serve static files in /uploads/
  if (req.method === 'GET' && pathname.startsWith('/uploads/')) {
    const filename = path.basename(pathname);
    const filepath = path.join(uploadsDir, filename);
    if (fs.existsSync(filepath)) {
      res.writeHead(200, { 'Content-Type': 'image/png' });
      return fs.createReadStream(filepath).pipe(res);
    } else {
      return sendJson(res, 404, { error: 'File Not Found' });
    }
  }

  let bodyStr = '';
  req.on('data', chunk => {
    bodyStr += chunk.toString();
  });

  req.on('end', () => {
    let body = {};
    if (bodyStr.trim()) {
      try {
        body = JSON.parse(bodyStr);
      } catch (err) {
        const params = new URLSearchParams(bodyStr);
        for (const [k, v] of params.entries()) {
          body[k] = v;
        }
      }
    }

    const host = req.headers.host || `localhost:${PORT}`;

    // REST Endpoint: POST /api/v1/auth/login
    if ((pathname === '/api/v1/auth/login' || pathname === '/login') && req.method === 'POST') {
      const email = (body.email || body.e || '').toLowerCase();
      const password = body.password || body.p || '';

      if (!email || !password) {
        return sendJson(res, 400, { error: 'Email and password required' });
      }

      let user = users.get(email);
      if (!user) {
        // Auto-register user for smooth testing
        const newPass = hashPassword(password);
        user = {
          email: email,
          salt: newPass.salt,
          hash: newPass.hash,
          username: email.split('@')[0],
          image_link: '',
          score: 50
        };
        users.set(email, user);
      } else {
        if (!verifyPassword(password, user.salt, user.hash)) {
          return sendJson(res, 401, { error: 'Wrong login credentials' });
        }
      }

      const token = generateJwt({ email: user.email, username: user.username });
      return sendJson(res, 200, {
        token: token,
        user: {
          username: user.username,
          email: user.email,
          image_link: user.image_link || ''
        }
      });
    }

    // REST Endpoint: POST /api/v1/auth/register
    if ((pathname === '/api/v1/auth/register' || pathname === '/register') && req.method === 'POST') {
      const username = body.username || body.u || '';
      const email = (body.email || body.e || '').toLowerCase();
      const password = body.password || body.p || '';
      const repeatPassword = body.repeatPassword || body.rp || '';

      if (!username || !email || !password || password !== repeatPassword) {
        return sendJson(res, 400, { error: 'Invalid registration data' });
      }

      if (users.has(email)) {
        return sendJson(res, 400, { error: 'User already exists' });
      }

      const newPass = hashPassword(password);
      const newUser = {
        email: email,
        salt: newPass.salt,
        hash: newPass.hash,
        username: username,
        image_link: '',
        score: 10
      };

      users.set(email, newUser);
      const token = generateJwt({ email: newUser.email, username: newUser.username });

      return sendJson(res, 201, {
        token: token,
        user: {
          username: newUser.username,
          email: newUser.email,
          image_link: newUser.image_link || ''
        }
      });
    }

    // REST Endpoint: POST /api/v1/auth/forgot-password
    if (pathname === '/api/v1/auth/forgot-password' && req.method === 'POST') {
      const email = (body.email || body.e || '').toLowerCase();
      if (!email || !users.has(email)) {
        return sendJson(res, 400, { error: 'Valid email required' });
      }
      return sendJson(res, 200, { message: 'Reset code sent' });
    }

    // REST Endpoint: POST /api/v1/auth/reset-password
    if (pathname === '/api/v1/auth/reset-password' && req.method === 'POST') {
      const code = body.code || body.rt;
      const password = body.password || body.p;
      const repeatPassword = body.repeatPassword || body.rp;

      if (!code || !password || password !== repeatPassword) {
        return sendJson(res, 400, { error: 'Invalid reset payload' });
      }
      return sendJson(res, 200, { message: 'Password reset successful' });
    }

    // --- Authenticated Endpoints (Require Bearer JWT Token) ---
    const rawToken = extractBearerToken(req) || body.token || body.t;
    const decodedToken = verifyJwt(rawToken);

    if (!decodedToken) {
      return sendJson(res, 401, { error: 'Unauthorized: Invalid or expired JWT token' });
    }

    const currentUser = users.get(decodedToken.email);

    // REST Endpoint: GET /api/v1/quiz/question
    if ((pathname === '/api/v1/quiz/question' || pathname === '/quiz') && (req.method === 'GET' || req.method === 'POST')) {
      // If POST answer payload
      if (req.method === 'POST' && (body.questionId || body.qid) && (body.rating || body.sid)) {
        currentQuizIndex = (currentQuizIndex + 1) % quizQuestions.length;
        return sendJson(res, 200, { message: 'Answer recorded' });
      }

      // Fetch current question
      const q = quizQuestions[currentQuizIndex % quizQuestions.length];
      return sendJson(res, 200, {
        questionId: q.id,
        title: q.title,
        question: q.question,
        answer: q.answer
      });
    }

    // REST Endpoint: POST /api/v1/quiz/answer
    if (pathname === '/api/v1/quiz/answer' && req.method === 'POST') {
      currentQuizIndex = (currentQuizIndex + 1) % quizQuestions.length;
      return sendJson(res, 200, { message: 'Answer recorded' });
    }

    // REST Endpoint: GET /api/v1/scoreboard
    if ((pathname === '/api/v1/scoreboard' || pathname === '/scoreboard') && (req.method === 'GET' || req.method === 'POST')) {
      const dataList = [];
      users.forEach((usr) => {
        dataList.push({
          username: usr.username,
          score: String(usr.score || 0),
          image_link: usr.image_link || ''
        });
      });

      if (dataList.length < 2) {
        dataList.push(
          { username: 'Alex', score: '120', image_link: '' },
          { username: 'Taylor', score: '95', image_link: '' },
          { username: 'Jordan', score: '80', image_link: '' }
        );
      }

      return sendJson(res, 200, { data: dataList });
    }

    // REST Endpoint: GET /api/v1/profile
    if (pathname === '/api/v1/profile' && req.method === 'GET') {
      return sendJson(res, 200, {
        username: currentUser ? currentUser.username : decodedToken.username,
        email: decodedToken.email,
        image_link: currentUser ? currentUser.image_link || '' : ''
      });
    }

    // REST Endpoint: POST /api/v1/profile/avatar
    if ((pathname === '/api/v1/profile/avatar' || pathname === '/profile' || pathname === '/profile/') && req.method === 'POST') {
      let imageBase64 = body.imageBase64 || body[crypto.createHash('md5').update('image').digest('hex')];

      const filename = `avatar_${Date.now()}.png`;
      const filepath = path.join(uploadsDir, filename);

      if (imageBase64) {
        fs.writeFileSync(filepath, Buffer.from(imageBase64, 'base64'));
      }

      const imageLink = `http://${host}/uploads/${filename}`;
      if (currentUser) {
        currentUser.image_link = imageLink;
      }

      return sendJson(res, 200, { image_link: imageLink });
    }

    return sendJson(res, 404, { error: 'API Endpoint Not Found' });
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`JWT REST API Server running on http://0.0.0.0:${PORT}`);
});
