require("dotenv").config();

const express = require("express");
const app = express();
const port = process.env.PORT || 3000;

// Import middlewares
const { applyMiddlewares, errorHandler } = require("./middlewares");

// Apply all middlewares
applyMiddlewares(app);

// Import routes
const routes = require("./routes");

// Use routes
app.use("/", routes);

// Catch-all route for undefined URLs
app.use((req, res) => {
  res.status(404).send("404 URL NOT FOUND");
});

// Apply error handler
app.use(errorHandler);

// Start the server
app.listen(port, () => {
  console.log(`🚀 Server is running at http://localhost:${port}`);
});
