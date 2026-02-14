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
    return res.status(400).json({ message: "All fields are required" });
  }

  try {
    // 1. Check if user is already approved in 'users'
    const [existingApproved] = await db.query("SELECT email FROM users WHERE email = ?", [email]);
    if (existingApproved) {
      return res.status(400).json({ message: "User is already registered and approved. Please login." });
    }

    // 2. Check if user is already waiting in 'pending_users'
    const [existingPending] = await db.query("SELECT email FROM pending_users WHERE email = ?", [email]);
    if (existingPending) {
      return res.status(400).json({ message: "Registration already submitted. Please wait for admin approval." });
    }

    // 3. If neither, proceed with registration
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    const mysql_qry = "INSERT INTO pending_users (email, password_hash) VALUES (?, ?)";

    await db.query(mysql_qry, [email, hashedPassword]);
    return res.status(201).json({ message: "Registration successful. Awaiting admin approval." });

  } catch (error) {
    console.error("Error during registration:", error);
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
    return res.status(400).json({ success: false, message: "Email is required" });
  }

  try {
    // 1. Check the main 'users' table
    const approvedQry = "SELECT role, approved_date FROM users WHERE email = ?";
    const approvedResults = await db.query(approvedQry, [email]);

    if (approvedResults.length > 0) {
      // User is approved (or at least exists in the main table)
      return res.status(200).json({
        success: true,
        status: "approved",
        role: approvedResults[0].role,
      });
    }

    // 2. If not found in 'users', check the 'pending_users' table
    const pendingQry = "SELECT email FROM pending_users WHERE email = ?";
    const pendingResults = await db.query(pendingQry, [email]);

    if (pendingResults.length > 0) {
      // User exists in pending table
      return res.status(200).json({
        success: true,
        status: "pending",
      });
    }

    // 3. Truly not found anywhere
    return res.status(404).json({
      success: false,
      status: "not_found",
      message: "User not found",
    });

  } catch (error) {
    console.error("Error fetching user status:", error);
    return res.status(500).json({ success: false, message: "Internal server error" });
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
