const jwt = require("jsonwebtoken");

function generateTokens(user) {
  const accessToken = jwt.sign(
    {
      user_id: user.user_id,
      email: user.email,
      role: user.role
    },
    process.env.ACCESS_SECRET,
    { expiresIn: "1h" }
  );

  const refreshToken = jwt.sign(
    { email: user.email },
    process.env.REFRESH_SECRET,
    { expiresIn: "7d" }
  );

  return { accessToken, refreshToken };
}

module.exports = {
  generateTokens
};
