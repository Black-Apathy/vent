const express = require("express");
const app = express();
const port = 3000;

// Import middlewares
const { applyMiddlewares, errorHandler } = require("./middlewares");

// Apply all middlewares
applyMiddlewares(app);


// Import routes
const routes = require("./routes");

// Use routes
app.use("/", routes);

// Catch-all route for undefined URLs
app.get("/:universalURL", (req, res) => {
  res.send("404 URL NOT FOUND");
});

// Apply error handler
app.use(errorHandler);

// Start the server
app.listen(port, () => {
  console.log(
    `🚀 Server is running`
  );
});
