function errorHandler(err, req, res, next) {
  console.error('[ServerError]', err.stack || err.message || err);
  res.status(500).json({
    error: 'Internal Server Error',
    message: process.env.NODE_ENV === 'development' ? err.message : undefined
  });
}

module.exports = errorHandler;
