const db = require("../utils/dbUtils");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const { generateTokens } = require("../utils/tokenUtils");

/**
 * Registers a new user by adding them to the pending_users table.
 * Expects: email, password in req.body
 */
exports.registerUser = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res
      .status(400)
      .json({ message: "All fields are required for registration" });
  }

  try {
    // Hash the password
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    const mysql_qry =
      "INSERT INTO pending_users (email, password_hash) VALUES (?, ?)";

    await db.query(mysql_qry, [email, hashedPassword]);
    return res
      .status(201)
      .json({ message: "Registration successful. Awaiting admin approval." });
  } catch (error) {
    console.error("Error hashing password:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

/**
 * Checks the approval status of a user.
 * Expects: email in req.query
 */
exports.checkUserStatus = async (req, res) => {
  const { email } = req.query;

  if (!email) {
    return res.status(400).json({
      success: false,
      message: "Email is required",
    });
  }

  const mysql_qry = "SELECT role, approved_date FROM users WHERE email = ?";

  try {
    const results = await db.query(mysql_qry, [email]);

    if (results.length > 0) {
      const user = results[0];

      if (user.approved_date) {
        return res.status(200).json({
          success: true,
          status: "approved",
          role: user.role,
        });
      } else {
        return res.status(200).json({
          success: true,
          status: "pending",
        });
      }
    } else {
      return res.status(404).json({
        success: false,
        status: "not_found",
        message: "User not found",
      });
    }
  } catch (error) {
    console.error("Error fetching user status:", error);
    return res.status(500).json({
      success: false,
      message: "Error fetching user status",
    });
  }
};

/**
 * Handles user login, checks credentials, and issues tokens.
 * Expects: email, password in req.body
 */
exports.handleLogin = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({
      success: false,
      message: "Email and password are required",
    });
  }

  try {
    const statusQuery = `
      SELECT user_id, email, role, password_hash, approved_date 
      FROM users WHERE email = ?
    `;
    const results = await db.query(statusQuery, [email]);

    if (results.length === 0) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      });
    }

    const user = results[0];

    if (!user.approved_date) {
      return res.status(403).json({
        success: false,
        message: "User not approved by admin",
      });
    }

    const isMatch = await bcrypt.compare(password, user.password_hash);

    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "Invalid credentials",
      });
    }

    const { accessToken, refreshToken } = generateTokens(user);

    const storeTokenQry = "UPDATE users SET refresh_token = ? WHERE email = ?";

    const storeResult = await db.query(storeTokenQry, [
      refreshToken,
      user.email,
    ]);

    if (storeResult.affectedRows === 0) {
      return res.status(500).json({
        success: false,
        message: "Error storing refresh token",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Login successful",
      accessToken,
      refreshToken,
      role: user.role,
    });
  } catch (error) {
    console.error("Error during login process:", error);
    return res.status(500).json({
      success: false,
      message: "An error occurred during login",
    });
  }
};

/**
 * Resets a user's password.
 * Expects: email, newPassword in req.body
 */
exports.resetPassword = async (req, res) => {
  const { email, newPassword } = req.body;

  if (!email || !newPassword) {
    return res.status(400).json({
      success: false,
      message: "Email and new password are required",
    });
  }

  try {
    const userQuery = "SELECT user_id FROM users WHERE email = ?";
    const results = await db.query(userQuery, [email]);

    if (results.length === 0) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      });
    }

    const SALT_ROUNDS = 10;
    const hashedPassword = await bcrypt.hash(newPassword, SALT_ROUNDS);

    const updateQuery = "UPDATE users SET password_hash = ? WHERE email = ?";
    const updateResult = await db.query(updateQuery, [hashedPassword, email]);

    if (updateResult.affectedRows === 0) {
      return res.status(500).json({
        success: false,
        message: "Error updating password",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Password reset successful",
    });
  } catch (error) {
    console.error("Error during forgot password process:", error);
    return res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};

/**
 * Issues a new access token using a valid refresh token.
 * Expects: refreshToken in req.body
 */
exports.refreshToken = async (req, res) => {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return res.status(400).json({
      success: false,
      message: "Refresh token missing",
    });
  }

  try {
    // Verify refresh token signature & expiry
    const decoded = jwt.verify(refreshToken, process.env.REFRESH_SECRET);
    const email = decoded.email;

    // Check if refresh token exists in DB
    const query =
      "SELECT user_id, email, role FROM users WHERE refresh_token = ?";
    const results = await db.query(query, [refreshToken]);

    if (results.length === 0) {
      return res.status(403).json({
        success: false,
        message: "Refresh token not found or already rotated",
      });
    }

    const user = results[0];
    const { accessToken, refreshToken: newRefreshToken } = generateTokens(user);

    // Update DB with new refresh token
    const updateQuery = "UPDATE users SET refresh_token = ? WHERE user_id = ?";
    await db.query(updateQuery, [newRefreshToken, user.user_id]);

    return res.json({
      success: true,
      accessToken,
      refreshToken: newRefreshToken,
    });
  } catch (err) {
    if (err.name === "JsonWebTokenError" || err.name === "TokenExpiredError") {
      return res.status(403).json({
        success: false,
        message: "Invalid or expired refresh token",
      });
    }

    console.error("Error in refreshToken:", err);
    return res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};
