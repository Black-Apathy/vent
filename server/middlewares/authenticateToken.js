const jwt = require("jsonwebtoken");

const authenticateToken = (req, res, next) => {
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1]; // Bearer <token>

  if (!token) return res.sendStatus(401); // No token

  jwt.verify(token, process.env.ACCESS_SECRET, (err, user) => {
    if (err) return res.sendStatus(403); // Invalid token

    req.user = user; // Save decoded payload to req.user
    next();
  });
};

module.exports = authenticateToken;
