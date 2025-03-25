// middlewares/helmetMiddleware.js
const helmet = require("helmet");

// Apply helmet for security headers
const helmetMiddleware = (app) => {
  app.use(helmet());
};

module.exports = helmetMiddleware;
