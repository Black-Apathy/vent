// middlewares/rateLimitMiddleware.js
const rateLimit = require("express-rate-limit");

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
});

// Apply rate limiting
const rateLimitMiddleware = (app) => {
  app.use(limiter);
};

module.exports = rateLimitMiddleware;
