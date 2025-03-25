// middlewares/index.js
const helmetMiddleware = require("./helmetMiddleware");
const bodyParserMiddleware = require("./bodyParserMiddleware");
const rateLimitMiddleware = require("./rateLimitMiddleware");
const errorHandler = require("./errorHandler");

const applyMiddlewares = (app) => {
  helmetMiddleware(app);
  bodyParserMiddleware(app);
  rateLimitMiddleware(app);
};

module.exports = {
  applyMiddlewares,
  errorHandler,
};
