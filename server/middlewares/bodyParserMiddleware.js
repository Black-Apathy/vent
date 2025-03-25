// middlewares/bodyParserMiddleware.js
const bodyParser = require("body-parser");

// Apply body parsing middleware
const bodyParserMiddleware = (app) => {
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({ extended: true }));
};

module.exports = bodyParserMiddleware;
